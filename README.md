# 🎨 DrawMe - Java Multi-Threaded Drawing App

## Overview

**DrawMe** is a Java Swing-based drawing application that demonstrates thread concurrency, GUI design, and file I/O. Users can draw with their mouse on a resizable canvas, save and load images, choose colors, and undo actions. The app also features an **autosave thread** with proper `synchronized` concurrency control to prevent data corruption during simultaneous access to the drawing canvas.

---

## ✨ Features

- ✅ Mouse-based freehand drawing using `BufferedImage`
- ✅ Rounded, modern buttons with hover and press feedback
- ✅ Manual Save/Load using `JFileChooser` with `.png` auto-extension
- ✅ AutoSave every 2 seconds (or configurable) using `Thread`
- ✅ `synchronized(lock)` concurrency control around canvas access
- ✅ Undo button (or `Ctrl+Z`) using canvas snapshot stack
- ✅ Color picker dialog with dynamic icon updates
- ✅ Real-time feedback via status bar
- ✅ Canvas resizes with window and preserves drawings
- ✅ Cleaned file I/O — autosaves go to `/saved/` folder

---

## 📦 How to Run

### 🧱 Prerequisites
- Java 8 or higher

### 🛠️ Compile & Run

```bash
javac *.java
java Main
