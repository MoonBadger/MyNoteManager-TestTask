import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class NoteManager {
    private List<Note> notes = new ArrayList<>();
    private static final Gson gson = (new GsonBuilder()).create();

    private int currentNoteId;

    private static NoteManager instance = new NoteManager();
    private NoteManager(){}
    public static NoteManager getInstance() {
        return instance;
    }


    public List<Note> getNotes() {
        return notes;
    }

    public int getCurrentNoteId() {
        return currentNoteId;
    }

    public Note getCurrentNote() {
        return notes.get(currentNoteId);
    }

    public void setCurrentNoteId(int currentNoteId) {
        this.currentNoteId = currentNoteId;
    }

    public void save(String path) {
        try (FileWriter fw = new FileWriter(path, false)) {
            File file = new File(path);
            //file.renameTo(file);
            String jsonString = gson.toJson(instance);
            fw.write(jsonString);
        } catch (Exception ignored) {}
    }

    public void load(String path) {
        try {
            Scanner scanner = new Scanner(new File(path));
            String jsonString = scanner.nextLine();
            instance.notes = gson.fromJson(jsonString, NoteManager.class).notes;
        } catch (Exception ignored) {}
    }
}
