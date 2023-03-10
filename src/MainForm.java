import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainForm extends JFrame {
    private JPanel panelMain;
    private JButton okButton;
    private JButton addButton;
    private JButton cancerButton;
    private JButton deleteButton;
    private JComboBox<String> comboBox;
    private JTextField titleTextField;
    private JTextArea mainTextArea;
    private JLabel timeLabel;

    private static final String JSON_PATH = "data/retention.json",
            DATE_FORMAT = "dd.MM.yyyy hh:mm",
            DEFAULT_TITLE = "<Без названия>",
            DEFAULT_ADD_TEXT= "Добавление новой заметки",
            DEFAULT_ADD_TITLE = "Заголовок заметки";

    private enum Mode {
        READ,
        EDIT,
        ADD
    }
    private Mode mode = Mode.READ;
    private final NoteManager manager = NoteManager.getInstance();

    public MainForm() {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(this.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        mainTextArea.setWrapStyleWord(true);
        okButton.setEnabled(false);

        manager.load(JSON_PATH);

        if(manager.getNotes().isEmpty()) {
            Note testNote = new Note("Заголовок", "текст");
            manager.setCurrentNoteId(0);
            manager.getNotes().add(testNote);
        }
        showNote();
        updateComboBox();

        mainTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                change();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}

            private void change() {
                if(mode != Mode.ADD) {
                    cancerButton.setEnabled(true);
                }
                okButton.setEnabled(true);
                if(mode == Mode.READ) {
                    mode = Mode.EDIT;
                } else {
                    manager.getCurrentNote().setContent(mainTextArea.getText());
                }
            }
        });

        titleTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                change();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}
            private void change() {
                if(mode != Mode.ADD) {
                    cancerButton.setEnabled(true);
                }
                okButton.setEnabled(true);
                if(mode == Mode.READ) {
                    mode = Mode.EDIT;
                } else {
                    manager.getCurrentNote().setTitle(titleTextField.getText());
                }
            }
        });

        okButton.addActionListener(e -> {
            if(mode == Mode.EDIT || mode == Mode.READ) {
                manager.getCurrentNote().setTitle(titleTextField.getText());
                manager.getCurrentNote().setContent(mainTextArea.getText());
                manager.save(JSON_PATH);
                showNote();
                okButton.setEnabled(false);
                cancerButton.setEnabled(false);
            } else if(mode == Mode.ADD) {
                String title = titleTextField.getText();
                String content = mainTextArea.getText();
                if(title.equals("")) {
                    title = DEFAULT_TITLE;
                }
                Note note = new Note(title, content);
                manager.getNotes().add(note);
                //manager.getNotes().sort(Note::compareTo);
                manager.setCurrentNoteId(manager.getNotes().size() - 1);
                addButton.setEnabled(true);
                comboBox.setEnabled(true);
                cancerButton.setEnabled(true);
                okButton.setText("ok");
                manager.save(JSON_PATH);
                update();
            }
            updateComboBox();
            mode = Mode.READ;
        });

        addButton.addActionListener(e -> {
            manager.load(JSON_PATH);
            update();
            mode = Mode.ADD;
            addButton.setEnabled(false);
            okButton.setEnabled(true);
            comboBox.setEnabled(false);
            okButton.setText("Добавить"); //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            titleTextField.setText(DEFAULT_ADD_TITLE);
            mainTextArea.setText(DEFAULT_ADD_TEXT);
            cancerButton.setEnabled(false);
        });

        cancerButton.addActionListener(e -> {
            manager.load(JSON_PATH);
            update();
            if(! (mode == Mode.ADD)) {
                okButton.setEnabled(false);
            }
            cancerButton.setEnabled(false);
        });

        comboBox.addItemListener(e -> {
            int ind = comboBox.getSelectedIndex();
            if(ind > 0) {
                manager.setCurrentNoteId(ind);
                showNote();
            }
        });
    }

    private void showNote() {
        Note note = manager.getCurrentNote();
        timeLabel.setText(noteToString(note));
        mainTextArea.setText(note.getContent());
        titleTextField.setText(note.getTitle());
    }

    private String noteToString(Note note) {
        Date date = note.getDatetime();
        return " ".repeat(5) + dateToString(date);
    }

    private String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(date);
    }

    private void updateComboBox() {
        comboBox.removeAllItems();
        List<Note> list = manager.getNotes();
        String[] items = new String[list.size()];
        for (int i = 0; i < items.length; i++) {
            Note note = list.get(i);
            items[i] = note.getTitle() +  " ".repeat(3) + "(" + dateToString(note.getDatetime()) + ")";
            comboBox.addItem(items[i]);
        }
        comboBox.setSelectedIndex(manager.getCurrentNoteId());
    }

    private void update() {
        showNote();
        updateComboBox();
    }
}
