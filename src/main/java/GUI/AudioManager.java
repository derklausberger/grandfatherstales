package GUI;

import objectClasses.Enemy;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class AudioManager {

    // HashMap to store the clips that have been loaded
    private static HashMap<String, Clip> clips;
    // The gap between looped sounds
    private static int gap;
    // The volume for the music
    private static float musicVolume;
    // The volume for the sounds
    private static float soundVolume;
    // A flag to indicate whether the sound is muted or not
    private static boolean mute = false;


    public static boolean playingSkeletonWalking;

    // Initializes the HashMap
    public static void init() {
        clips = new HashMap<String, Clip>();
        gap = 0;
    }

    // Loads a sound into the HashMap
    public static void load(String source, String name) {
        // If the sound has already been loaded, does nothing
        if (clips.get(name) != null) return;
        Clip clip;
        try {
            // Creates an AudioInputStream and a Clip to hold the sound
            AudioInputStream ais =
                    AudioSystem.getAudioInputStream(new File(source).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(ais);
            // Adds the clip to the HashMap
            clips.put(name, clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sets the volume of sounds
    // Takes values between 0 and 1
    public static void setSoundVolume(float volume) {
        soundVolume = 20f * (float) Math.log10(volume);
        // Loops through the clips and looks for sounds by name
        for (String key : clips.keySet()) {
            Clip c = clips.get(key);
            if (key.startsWith("S")) {
                setVolume(c, soundVolume);
            }
        }
    }

    // Sets the volume of music
    // Takes values between 0 and 1
    public static void setMusicVolume(float volume) {
        musicVolume = 20f * (float) Math.log10(volume);
        // Loops through the clips and looks for music by name
        for (String key : clips.keySet()) {
            Clip c = clips.get(key);
            if (key.startsWith("M")) {
                setVolume(c, musicVolume);
            }
        }
    }

    // Sets the volume of a specific Clip
    public static void setVolume(Clip c, float volume) {
        if (c == null || !c.isOpen()) return;
        FloatControl fc = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
        fc.setValue(volume);
    }

    // Gets the volume of sounds
    public static float getSoundVolume() {
        return soundVolume;
    }

    // Gets the volume of music
    public static float getMusicVolume() {
        return musicVolume;
    }

    // Plays a sound from the beginning
    public static void play(String name) {
        play(name, gap);
    }

    // Plays a sound from a specified frame
    public static void play(String name, int startFrame) {
        // If the sound is muted, does nothing
        if (mute) return;
        Clip c = clips.get(name);
        // If the sound does not exist, does nothing
        if (c == null) return;
        // If the sound is already playing, stops it
        if (c.isRunning()) c.stop();

        //FloatControl floatControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);


        // Sets the volume and position and starts the sound
        if (name.startsWith("S")) {
            setVolume(c, soundVolume);
        } else if (name.startsWith("M")) {
            setVolume(c, musicVolume);
        }
        c.setFramePosition(startFrame);
        while (!c.isRunning()) c.start();
    }

    // Stops a sound
    public static void stop(String name) {
        // If the sound does not exist, does nothing
        if (clips.get(name) == null) return;

        // If the sound is already stopped, does nothing
        if (clips.get(name).isRunning()) clips.get(name).stop();
    }

    // Stops all sounds
    public static void stopAll() {

        for (Clip c : clips.values()) {
            if (c.isRunning()) c.stop();
        }
    }

    // Resumes a sound from where it was paused
    public static void resume(String name) {
        // If the sound is muted, does nothing
        if (mute) return;
        // If the sound is already playing, does nothing
        if (clips.get(name).isRunning()) return;

        // Sets the volume and starts the sound
        if (name.startsWith("S")) {
            setVolume(clips.get(name), soundVolume);
        } else if (name.startsWith("M")) {
            setVolume(clips.get(name), musicVolume);
        }
        clips.get(name).start();
    }

    // Loops a sound from the beginning
    public static void loop(String name) {
        loop(name, gap, gap, clips.get(name).getFrameLength() - 1);
    }

    // Loops a sound from a specified frame
    public static void loop(String name, int frame) {
        loop(name, frame, gap, clips.get(name).getFrameLength() - 1);
    }

    // Loops a sound from a specified start and end frame
    public static void loop(String name, int start, int end) {
        loop(name, gap, start, end);
    }

    // Loops a sound from a specified frame, start and end frame
    public static void loop(String name, int startFrame, int loopStart, int loopEnd) {
        // If the sound is muted, does nothing
        if (mute) return;
        Clip c = clips.get(name);
        // If the sound does not exist, does nothing
        if (c == null) return;
        // If the sound is already playing, stops it
        if (c.isRunning()) return;

        // Sets the volume, loop points, position and then loops the sound continuously
        if (name.startsWith("S")) {
            setVolume(c, soundVolume);
        } else if (name.startsWith("M")) {
            setVolume(c, musicVolume);
        }
        c.setLoopPoints(loopStart, loopEnd);
        c.setFramePosition(startFrame);
        c.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // Sets the position of a sound
    public static void setPosition(String name, int frame) {
        clips.get(name).setFramePosition(frame);
    }

    // Gets the number of frames in a sound
    public static int getFrames(String name) {
        return clips.get(name).getFrameLength();
    }

    // Gets the current position of a sound
    public static int getPosition(String name) {
        return clips.get(name).getFramePosition();
    }

    // Sets the gap between looped sounds
    public static void setGap(int g) {
        gap = g;
    }

    // Mutes or unmutes the sound
    public static void setMute(boolean m) {
        mute = m;

        for (String key : clips.keySet()) {
            if (m) stop(key);
            else play("M - mainTheme");
        }
    }

    // Disposes all clips to free up memory space
    public static void closeAllClips() {
        for (Clip c : clips.values()) {
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Clears the HashMap
        clips.clear();
    }
}

