![nmania logo](/res/ui/nmania-logo-1x.png)
# nmania
Open source piano-like rhythm game for J2ME, compatible with osu!mania beatmaps.

![](/info/sh2.png)

## System requirements
### Minimal
- JVM/KVM on your device with CLDC 1.1
- MIDP 2.0 support
- JSR-75 (FC API), JSR-135 (MMAPI)
- Support of MPEG audio playback
- At least 2 mb of RAM (likely this won't be enough, 8-12 is okay)
- Physical keyboard / ability to connect one
- Ability to handle at least 2 simultaneously held buttons (4+ will be okay)
- Enough perfomance. In case of Symbian, ~450mhz CPU is okay (f.e., OMAP 2430/31), but it depends on JVM implementation and CPU capatibilities.
### Recommended devices
9.3 or ^3 Symbian device with keyboard is recommended (E5, E6, E7, E72).
### Emulators
- `J2ME Loader` has major problems with native UI, but playable. Use OTG or BT keyboard. 640x360 is recommended. Your working folder is likely `/storage/emulated/0/Data/Sounds/nmania/`.
- `PhoneME` is partially playable, has major problems with input/gameplay/music synchronization and files.
- `KEmulator` is not compatible due to broken multimedia API.
- [`KEmulator nnmod`](http://nnproject.cc/kem/) is partially playable. Skin settings are not functional, multihold is not propertly supported.

## Join our chat in TG!
https://t.me/nnmidletschat

## How to play
Go to osu!direct or it's mirror and download maps that you want to play (or copy them from stable's `Songs` folder). Extract your maps to `C:/Data/Sounds/nmania/` (if there is no such path, create folders manually) like stable does - one nested folder for each mapset. 

If you have a standalone `nmania` beatmap, you need to compose a BMS yourself. Create a nested folder, place the beatmap file here. Find the music track and background image for it, put them in the same folder with the beatmap (keep eye on files' names, rename them if needed).

Launch the game and play. You may want to adjust scroll speed, dim and sound effects in `settings`, adjust look of the game in `skinning`, or read this manual again in `info`.

If the game is too laggy even with flat skinning and without sounds, there is likely something wrong with your device, _WORKSFORME_.

### Rich skinning structure
It's written inside the game. You can also explore a [template](/info/richSkinTemplate.zip).

### Language setting
To apply a localization, pack your files into jar under names `CATEGORY_LANG.txt` as english files named. In game settings, enter the `LANG` postfix. Refer to source code if it doesn't work.

### Flush rate troubles
Default HUD rendering requires 3 flushes in a game frame. This can limit FPS to 20 or even 16 on some devices. Try to enable `fullscreen flush`. Now there will be 1 flush in 1 frame, but yeah, the whole screen. Still bad? Disable HUD at all (`draw counters`) (don't forget to disable `fullscreen flush` too!). Now you will have 1 partial flush in 1 frame. This should be enough. And yeah, buy a normal device like E5 (:

## Manual building
Find a SDK for your device, install it. Look for futher instructions in it's documentation (you want to package a `MIDlet suite`).

If you want an IDE, i can recommend `Eclipse IDE for Java` with `Mobile Tools for Java` (`MTJ`) plugin for it. You also need J2SE 1.5 and an SDK for your device. Set up them to work together and create a JAR package from `Application Descriptor` screen.
