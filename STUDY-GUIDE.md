# AlgoVision — Study & Interview Guide

This is your "understand it well enough to defend it" companion. Read it once
top-to-bottom, then keep it open while you click around the running app.

---

## 1. What the app actually does (one paragraph)

AlgoVision is a desktop app that **animates how algorithms work**. You pick an
algorithm (say Bubble Sort), it draws the array as colored bars, and then it
plays the algorithm step by step — bars light up when they're compared, swap
places when they're swapped, and turn green when they're locked in their final
position. It does the same for searching (Linear, Binary) and graph traversal
(BFS, DFS). Along the way it shows live counts, the pseudocode with the current
line highlighted, and time/space complexity.

---

## 2. The ONE idea that makes the whole thing work

If you remember nothing else, remember this. It is the answer to "how did you
build the animation?" and it's what makes the project look senior.

**The algorithm never touches the screen. It records a script; the UI plays it back.**

Concretely, there are two phases:

1. **Record phase** — The algorithm runs to completion *instantly* (no delays,
   no animation). But every time it does something interesting — a comparison, a
   swap — it appends a small `AlgorithmStep` object to a list. The result is a
   "script" of everything that happened, in order.

2. **Playback phase** — A `javax.swing.Timer` fires every few milliseconds. On
   each tick it takes the next `AlgorithmStep` from the list and updates the UI
   to match it (light up these two bars, swap those two, etc.).

Why this design is the right one (and the interview-winning point):

- **It separates logic from rendering.** `SortingService` knows *nothing* about
  colors, bars, or Swing. You could reuse it in a web app or a command-line tool.
- **Pause / resume / step / reset / speed become trivial.** They're just
  "stop the timer", "start the timer", "advance one item", "go back to index 0",
  "change the timer delay". No threads to coordinate, no risk of a half-finished
  swap.
- **It's thread-safe by construction.** All UI updates happen on the Swing Event
  Dispatch Thread (EDT) because the Timer fires on the EDT. The naive approach —
  running the sort on a background thread with `Thread.sleep()` between steps —
  is a classic source of race conditions and flicker. We avoid that entirely.

If an interviewer asks "why not just put `Thread.sleep()` inside the sort?",
that paragraph above is your answer.

---

## 3. The folder structure (and why it's split this way)

```
src/
  model/     "what" — plain data, no behavior, no UI
    StepType.java        an enum: COMPARE, SWAP, OVERWRITE, MARK_SORTED, ...
    AlgorithmStep.java   one recorded action (type + a couple of indices)
    AlgorithmInfo.java   static text: definitions, complexity, pseudocode
    AppState.java        a tiny "memory" of the last run + user preferences
  service/   "how" — the algorithms themselves, pure logic
    SortingService.java   bubble/selection/insertion/merge/quick/heap
    SearchingService.java linear/binary
    GraphService.java     bfs/dfs over a fixed 7-node graph
  util/      shared helpers
    ColorPalette.java    every color in one place (themes live here)
    UIHelper.java        font + button factory methods
  ui/        "show" — everything Swing
    MainFrame.java       the window; holds the sidebar + the card-switching area
    SidebarPanel.java    left navigation
    TopBar.java          the header strip
    BarCanvas.java       draws the array as bars (Java2D)
    GraphCanvas.java     draws the graph nodes + edges
    StepPlayer.java      the Timer that plays the script back
    SortingPanel.java    the Sorting screen (controls + canvas + info)
    SearchingPanel.java  the Searching screen
    GraphPanel.java      the Graph screen
    DashboardPanel.java  the landing screen with last-run stats
    SettingsPanel.java   color scheme + default size/speed
    AboutPanel.java      static info card
    RoundedPanel.java    a JPanel that paints a rounded background
    StatCard.java        the little "caption + big number" card
  Main.java   entry point: launches MainFrame on the EDT
```

This is the **layered / single-responsibility** pattern. The rule of thumb:
`model` doesn't import `ui`, `service` doesn't import `ui`. Dependencies point
*inward* toward plain data. That's what lets you say "it's a clean architecture."

---

## 4. Follow one click all the way through (Bubble Sort)

This is the most useful thing to be able to narrate. Trace it once and you
understand the whole app.

**Step A — you click "Start" in `SortingPanel`.**
`start()` clones the original array (so the bars you see don't change yet) and
calls `SortingService.run("Bubble Sort", clone)`.

**Step B — `SortingService.bubble()` records the script.**
```java
for (int i = 0; i < n - 1; i++) {
    for (int j = 0; j < n - 1 - i; j++) {
        s.add(AlgorithmStep.compare(j, j + 1, 2, "Comparing ..."));   // record
        if (a[j] > a[j + 1]) {
            s.add(AlgorithmStep.swap(j, j + 1, 3, "Swapping ..."));   // record
            swap(a, j, j + 1);                                        // do it
        }
    }
    s.add(AlgorithmStep.mark(n - 1 - i, 4, "Position locked"));       // record
}
```
Notice the pattern: **record the intention, then perform it.** By the time the
loop ends, `s` is a `List<AlgorithmStep>` describing the entire sort. The actual
array `a` is already fully sorted — but the *bars on screen* haven't moved yet.

**Step C — `start()` hands the script to the player.**
```java
player.load(steps);
player.play();
```

**Step D — `StepPlayer` ticks.** Every `delayMs` it calls `tick()`, which pulls
the next step and calls back into `SortingPanel.applyStep(step)`.

**Step E — `applyStep()` updates the UI to match one step.**
```java
switch (s.type) {
    case COMPARE: comparisons++; canvas.setCompare(s.a, s.b); break;
    case SWAP:    swaps++; /* swap data[s.a] and data[s.b] */ canvas.setSwap(s.a, s.b); break;
    case MARK_SORTED: canvas.addSorted(s.a); break;
}
highlightCode(s.codeLine);   // light up the matching pseudocode line
updateStats();               // refresh the comparison/swap counters
canvas.repaint();            // ask Swing to redraw the bars
```
Crucially, **the panel mutates its own `data` array exactly the way the service
mutated its clone.** Same swaps, same order — so the bars end up matching the
real result.

**Step F — when the last step plays, `onFinish()` runs.** It paints every bar
green and saves the run into `AppState` (this is what the Dashboard reads later).

That's the entire lifecycle. Searching and Graph follow the identical shape,
just with different step types (PROBE/RANGE/FOUND for search, VISIT_NODE/FRONTIER
for graphs).

---

## 5. How to edit things (concrete recipes)

### Change the colors / add a theme
Open `util/ColorPalette.java`. Every color is a constant there. The bar colors
for the three schemes live in the `schemeColors(int)` method. Change a hex value,
recompile, done. (The Settings screen already flips between scheme 0/1/2 live.)

### Change the bar value range
`SortingPanel.generate()`:
```java
original[i] = 5 + rng.nextInt(96);   // values 5..100
```
Change `96` to make taller/shorter bars.

### Change the animation speed range
The speed slider maps to the timer delay in `start()`:
```java
player.setDelay(101 - speedSlider.getValue());  // higher slider = smaller delay = faster
```

### Add a new sorting algorithm (the big one)
1. In `SortingService.java`, write a `myAlgo(int[] a)` method that returns a
   `List<AlgorithmStep>`, recording a step whenever you compare/swap/overwrite.
2. Add a case to the `run(...)` switch: `case "My Algo": return myAlgo(a);`
3. Add `"My Algo"` to the combo box in `SortingPanel` (the `algoBox` array).
4. Add an entry in `AlgorithmInfo.of(...)` so its definition/complexity/pseudocode
   show up.
That's it — the animation, controls, and stats all work automatically because
they only speak the `AlgorithmStep` "language."

### Change the graph
`GraphService.java` has `NODES`, `EDGES`, and `POS` (node positions) as constants
at the top. Edit those to change the graph shape.

### Put your name in the About screen
`ui/AboutPanel.java` — replace `Your Name`, `github.com/yourusername`,
`yourportfolio.com` with your real details.

---

## 6. Interview Q&A bank

**Q: Give me a high-level overview of this project.**
A modern Java Swing desktop app that visualizes sorting, searching, and graph
algorithms with step-by-step animation, live statistics, complexity analysis,
and pseudocode highlighting. Built with pure Java, Swing, and Java2D — no
external libraries, no build tools. Around 2,500 lines across a clean layered
architecture.

**Q: How does the animation work? Did you use threads?**
No raw threads. I use a record-then-replay design: the algorithm runs to
completion and records a list of `AlgorithmStep` objects; a `javax.swing.Timer`
then replays those steps one per tick. This keeps all UI updates on the Event
Dispatch Thread, so it's thread-safe, and it makes pause/resume/step/speed
trivial — they're just operations on the timer and an index.

**Q: Why is that better than `Thread.sleep()` in the sort?**
Two reasons. First, `Thread.sleep()` on the EDT freezes the whole UI; doing it on
a worker thread then touching Swing components from that thread is a race
condition (Swing isn't thread-safe). Second, with a recorded script, pause/step/
reset are free. With the sleep approach you'd have to thread interrupts and
shared flags through every algorithm — fragile and hard to test.

**Q: How is the code organized?**
Layered with single responsibility. `model` is plain data, `service` is the
algorithms (pure logic, no UI), `util` is shared helpers, `ui` is all Swing.
Dependencies point inward — `service` and `model` never import `ui` — so the
algorithms are reusable and unit-testable on their own.

**Q: How did you test it / how do you know the algorithms are correct?**
The services are decoupled from the UI, so I tested them headlessly: run each
sort on hundreds of random arrays and assert the output is sorted *and* a
permutation of the input (no lost/duplicated values); verify binary search finds
known indices; verify BFS/DFS visit every reachable node. (Mention you ran ~1,000+
trials.)

**Q: What's an `AlgorithmStep`?**
An immutable value object describing one thing the algorithm did: a type (from a
`StepType` enum like COMPARE/SWAP/MARK_SORTED), one or two indices, and an
optional message + which pseudocode line is active. Immutability means a recorded
script can't be accidentally mutated during playback.

**Q: How does the pseudocode highlighting stay in sync?**
Each recorded step carries a `codeLine` number. When that step plays, the panel
highlights the matching pseudocode label. So the highlight is driven by the same
script as the bars — they can't drift apart.

**Q: How does Binary Search show the "search window"?**
Binary search emits RANGE steps as it narrows lo/hi. The canvas dims bars outside
the current range so you can literally watch the window shrink. The array is kept
sorted (I sort it on generate) so binary search is valid.

**Q: What design patterns did you use?**
- *Strategy-ish* dispatch: `run(name, ...)` switches to the chosen algorithm.
- *Factory methods*: `AlgorithmStep.compare(...)`, `.swap(...)` etc. keep the
  services readable.
- *Observer/callback*: the player calls back via `Consumer<AlgorithmStep>` and a
  `Runnable onFinish`, so it doesn't depend on any specific panel.
- *Single source of truth*: `ColorPalette` and `AppState` centralize styling and
  shared state.

**Q: What was the hardest bug?**
A `StackOverflowError` from infinite recursion in navigation: switching screens
visually highlighted the sidebar, but the highlight method also re-fired the
"navigate" callback, which switched screens again — forever. I fixed it by
splitting responsibilities: one method only changes the highlight (no callback),
and only a real user click triggers navigation. Good lesson in not letting a
state-change method re-trigger the event that caused it.

**Q: The dashboard shows an "Exec Time" — what is it measuring?**
The actual time the algorithm takes to run (the record phase), measured with
`System.nanoTime()` around the call — not the animation. Animation duration just
reflects the speed slider and would be misleading as a performance number. So a
45-element sort reports a fraction of a millisecond, and you can see Bubble/
Selection cost more than Merge/Insertion on the same input. (Caveat I'd mention:
the very first run in a session can read a bit high because of JVM warmup / JIT;
it settles after that.)

**Q: What would you improve / what's next?**
Three things: (1) make Searching and Graph record their own dashboard stats —
right now only Sorting does; (2) a full app-wide light/blue theme (the bar color
scheme already switches, but recoloring every surface is a separate pass);
(3) more algorithms — Dijkstra, A*, AVL/Red-Black trees, DP visualizations.

**Q: Why Swing and not JavaFX?**
Swing ships with the JDK, needs zero setup, and Java2D gives me full control over
the custom bar/graph rendering. For a self-contained portfolio app where I wanted
no dependencies, it was the pragmatic choice. (If pushed: JavaFX has nicer
styling/animation APIs and would be a reasonable rewrite target.)

**Q: Is it thread-safe?**
Yes, by design — all rendering and state changes happen on the EDT via the Swing
Timer. I never spawn threads that touch UI components.

---

## 7. Known limitations (own them — don't hide them)

1. **Dashboard stats come from the Sorting screen.** Sorting publishes its array
   size, comparisons, swaps, compute time, and algorithm to the Dashboard live
   (the moment you generate or run). Searching and Graph currently publish only
   the algorithm name, so after a search/graph run the numeric cards read "—".
   Easy next step: have those panels publish their own metrics (search has a real
   array size + comparison count; graph could show nodes visited).
2. **No full light theme yet.** Bar color scheme switches live; full surface
   reskin is on the roadmap.
3. **Fixed graph.** The graph is a hardcoded 7-node example, not user-editable.
4. **In-memory only.** Settings persist for the session, not across restarts
   (no file/db). Intentional — keeps it dependency-free.

Being able to list these *before the interviewer finds them* reads as maturity.

---

## 8. 30-second elevator pitch (memorize this)

"AlgoVision is a Java Swing app that animates how algorithms work — you watch
bars compare, swap, and lock into place while live counters and highlighted
pseudocode follow along. The core design is record-then-replay: the algorithm
runs and records a script of steps, and a Swing Timer plays that script back.
That cleanly separates algorithm logic from rendering, keeps everything on the
UI thread so it's thread-safe, and makes pause/step/speed controls trivial. It's
pure Java with no external libraries, organized in a layered architecture, and
the algorithms are unit-tested independently of the UI."
