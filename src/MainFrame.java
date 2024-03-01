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

	private DealerPanel dealerPanel1;
	private DealerPanel dealerPanel2;
	private JPanel startPanel; // Added startPanel
	private Timer textAnimationTimer; // Added timer for text animation
	private JLabel startLabel; // Modified to make it a class variable

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

		// Initialize DealerPanels
		dealerPanel1 = new DealerPanel(150, 50);
		dealerPanel2 = new DealerPanel(650, 50);

		// Create and add startPanel
		startPanel = new JPanel();
		startPanel.setBounds(0, 0, this.getWidth(), this.getHeight()); // Full width
		startPanel.setBackground(Color.BLACK); // Set background color

		// Create a layout manager for the startPanel
		Box verticalBox = Box.createVerticalBox();
		startPanel.add(verticalBox);

		// Create a label for the text
		startLabel = new JLabel("BLACK JACK");
		startLabel.setForeground(Color.WHITE); // Set text color
		startLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
		startLabel.setFont(new Font(startLabel.getFont().getName(), Font.PLAIN, 200)); // Set font size to 200 pixels
		// Set initial font size

		// Add a strut for spacing between the text and the button
		verticalBox.add(Box.createVerticalStrut(300));

		// Create the 'Start Game' button
		JButton startButton = new JButton("Start Game");
		startButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame(); // Call a method to start the game
			}
		});

		// Add components to the verticalBox
		verticalBox.add(startLabel);
		verticalBox.add(startButton);

		// Add the startPanel to the main frame
		add(startPanel);

		// Initialize the text animation timer
		textAnimationTimer = new Timer(50, new ActionListener() {
			private float fontSize = 20; // Initial font size

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fontSize < 30) {
					fontSize += 0.5; // Adjust the speed of font size increase
					startLabel.setFont(startLabel.getFont().deriveFont(fontSize));
				} else {
					textAnimationTimer.stop(); // Stop the timer when finished
				}
			}
		});
		textAnimationTimer.start(); // Start the timer
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

	// Method to start the game
	private void startGame() {
		// Remove startPanel and add dealer panels or perform other game initialization
		remove(startPanel);
		add(dealerPanel1);
		// Add other game initialization logic here

		// Repaint and revalidate the frame
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
	// DealerPanel class for displaying dealer's cards

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
	// ImagePanel class for displaying background image

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
