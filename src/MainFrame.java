import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;

public class MainFrame extends JFrame {

	private ImagePanel bgImagePanel;
	private String[] predefinedBackgrounds = {"background1.png", "background2.jpeg", "background3.jpeg"};
	private int currentBackgroundIndex = 0;

	private DealerPanel dealerPanel1;
	private DealerPanel dealerPanel2;
	private JPanel startPanel;
	private Timer textAnimationTimer;
	private JLabel startLabel;
	private JButton chooseBackgroundButton;
	private JButton toggleMusicButton;
	private Clip backgroundMusic;

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
		chooseBackgroundButton = new JButton("Choose Background");
		chooseBackgroundButton.setBounds(10, 10, 150, 30);
		chooseBackgroundButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseBackground();
				playButtonClickSound();
			}
		});
		add(chooseBackgroundButton);

		// Button to toggle background music
		toggleMusicButton = new JButton("Toggle Music");
		toggleMusicButton.setBounds(170, 10, 150, 30);
		toggleMusicButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleBackgroundMusic();
			}
		});
		add(toggleMusicButton);

		// Initialize background music
		initializeBackgroundMusic();

		// Initialize DealerPanels
		dealerPanel1 = new DealerPanel(150, 50);
		dealerPanel2 = new DealerPanel(650, 50);

		// Create and add startPanel
		startPanel = new JPanel();
		startPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		startPanel.setBackground(Color.BLACK);

		Box verticalBox = Box.createVerticalBox();
		startPanel.add(verticalBox);

		startLabel = new JLabel("BLACK JACK");
		startLabel.setForeground(Color.WHITE);
		startLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		startLabel.setFont(new Font(startLabel.getFont().getName(), Font.PLAIN, 200));

		verticalBox.add(Box.createVerticalStrut(300));

		JButton startButton = new JButton("Start Game");
		startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});

		verticalBox.add(startLabel);
		verticalBox.add(startButton);

		add(startPanel);

		textAnimationTimer = new Timer(50, new ActionListener() {
			private float fontSize = 20;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fontSize < 30) {
					fontSize += 0.5;
					startLabel.setFont(startLabel.getFont().deriveFont(fontSize));
				} else {
					textAnimationTimer.stop();
				}
			}
		});
		textAnimationTimer.start();
	}

	private void initializeBackgroundMusic() {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("bg-music.wav"));
			backgroundMusic = AudioSystem.getClip();
			backgroundMusic.open(audioInputStream);

			// Adjust volume using FloatControl
			FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
			// -20.0f represents a decrease in volume. You can adjust this value as needed.
			gainControl.setValue(-20.0f);

			backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void toggleBackgroundMusic() {
		if (backgroundMusic.isRunning()) {
			backgroundMusic.stop();
			toggleMusicButton.setText("Resume Music");
		} else {
			backgroundMusic.start();
			backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
			toggleMusicButton.setText("Pause Music");
		}
	}

	private void chooseBackground() {
		currentBackgroundIndex = (currentBackgroundIndex + 1) % predefinedBackgrounds.length;
		bgImagePanel.setImage(predefinedBackgrounds[currentBackgroundIndex]);
		repaint();
	}

	private void playButtonClickSound() {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("button_click.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startGame() {
		remove(startPanel);
		add(dealerPanel1);
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainFrame mainFrame = new MainFrame();
			mainFrame.setVisible(true);
		});
	}
}

class DealerPanel extends JPanel {
	private ImagePanel cardPanel;

	public DealerPanel(int x, int y) {
		setLayout(null);
		setBounds(x, y, 200, 200);
		cardPanel = new ImagePanel("backCover.png");
		cardPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		add(cardPanel);
	}

	public void setCardImage(String imgStr) {
		cardPanel.setImage(imgStr);
		repaint();
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
