# TODO

---

## Block

- [ ] Add a crafting recipe
- [x] Implement saving block data to server
- [x] Fix sounds continuing to play when the sound block is broken
- [ ] Implement checkbox sound stopping behaviour:
    - [ ] Left checkbox (StopOnEntry) stops on transition from previous stage to current stage
    - [ ] Right checkbox (StopOnExit) stops on transition from current stage to next stage
- [x] Implement force-powered state; bypasses redstone signal requirement

## GUI

- [x] Implement the sound entry scrollbar
- [x] Implement mouse-wheel scrolling
- [ ] Add mouse-over GUI tooltips to:
  - [ ] Text-fields
  - [ ] Buttons / arrows
  - [ ] Checkboxes
  - [ ] Tabs
- [x] Add texts to GUI tabs (Config, Intro, Loop, Outro)
- [ ] Implement general tab:
  - [x] Force-powered checkbox
  - [ ] Master X offset
  - [ ] Master Y offset
  - [ ] Master Z offset
  - [x] Loop delay ticks count
  - [x] Master pitch multiplier
  - [x] Master volume multiplier
  - [x] Master distance multiplier
- [x] Implement tabbing through text fields (forwards/backwards)
