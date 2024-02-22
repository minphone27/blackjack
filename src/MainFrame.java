import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class MainFrame extends JFrame {

	private ImagePanel bgImagePanel;
	private String[] predefinedBackgrounds = {"background1.png", "background2.jpeg", "background3.jpeg"};
	private int currentBackgroundIndex = 0;

	MainFrame() {
		setTitle("Blackjack");
		setSize(900, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);

		// Initial background
		bgImagePanel = new ImagePanel(predefinedBackgrounds[currentBackgroundIndex]);
		bgImagePanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		setContentPane(bgImagePanel);

		// Button to choose background
		JButton chooseBackgroundButton = new JButton("Choose Background");
		chooseBackgroundButton.setBounds(10, 10, 150, 30);
		chooseBackgroundButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseBackground();
				playButtonClickSound();
			}
		});
		add(chooseBackgroundButton);
	}

	private void chooseBackground() {
		// Cycle through predefined backgrounds
		currentBackgroundIndex = (currentBackgroundIndex + 1) % predefinedBackgrounds.length;
		bgImagePanel.setImage(predefinedBackgrounds[currentBackgroundIndex]);
		repaint();
	}

	private void playButtonClickSound() {
		try {
			// Load the sound file
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("button_click.wav"));

			// Get a clip resource
			Clip clip = AudioSystem.getClip();

			// Open audio clip and load samples from the audio input stream
			clip.open(audioInputStream);

			// Start playing the clip
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainFrame mainFrame = new MainFrame();
			mainFrame.setVisible(true);
		});
	}
}

class ImagePanel extends JPanel {
	private Image img;

	public ImagePanel(String imgStr) {
		this.img = new ImageIcon(imgStr).getImage();
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setSize(size);
		setLayout(null);
	}

	public void setImage(String imgStr) {
		this.img = new ImageIcon(imgStr).getImage();
		repaint();
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
	}
}
