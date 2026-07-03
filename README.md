# NekoSense

Cross-game sensitivity & acceleration calibration tool for mobile FPS games.

Calibrate your sensitivity between different mobile FPS games using automatic swipe + screen detection. No game-specific data required — works with any game.

## How It Works

1. Set your known sensitivity in Game A
2. NekoSense auto-swipes and detects 360° rotation via screen capture
3. Repeat for Game B
4. App calculates the equivalent sensitivity and acceleration values

## Requirements

- Android 8.0+ (API 26)
- [Shizuku](https://shizuku.rikka.app/) installed and running
- Screen Capture permission
- Overlay permission

## Build

```bash
./gradlew assembleRelease
```

## License

MIT
