import javax.swing.*;
import java.awt.event.*;

public class ConfirmationForm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel textLabel;

    private final String TITLE = "Подтвердите решение";
    private final String MESSAGE = "Вы действительно хотите удалить заметку?";

    private MainForm parent;

    public ConfirmationForm(MainForm parent) {
        this.parent = parent;
        textLabel = new JLabel(MESSAGE);
        add(textLabel);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pack();

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        setTitle(TITLE);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e ->
                onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        parent.setConfirm(true);
        dispose();
    }

    private void onCancel() {
        parent.setConfirm(false);
        dispose();
    }
}
