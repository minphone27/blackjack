import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class Blackjack {

	private static JFrame frame = new MainFrame();
	private static CardGroup deck, dealerCards, playerCards;
	private static CardGroupPanel dealerCardPanel = null, playerCardPanel = null;
	private static Card dealerHiddenCard;
	private static double balance = 0.0;
	private static int betAmount = 0, roundCount = 0;
	private static JButton btnSwapCards;
	private static JComboBox<String> playerCardComboBox;
	private static JComboBox<String> dealerCardComboBox;

	private static JTextField tfBalance;
	private static JLabel lblInitialBalance;
	private static JButton btnNewGame;
	private static JButton btnEndGame;
	private static JTextField tfBetAmount;
	private static JLabel lblEnterBet;
	private static JButton btnDeal;
	private static JLabel lblCurrentBalance;
	private static JLabel lblBalanceAmount;
	private static JLabel lblDealer;
	private static JLabel lblPlayer;
	private static JButton btnHit;
	private static JButton btnStand;
	private static JLabel lblBetAmount;
	private static JLabel lblBetAmountDesc;
	private static JLabel lblInfo;
	private static JButton btnContinue;
	private static JLabel lblShuffleInfo = null;

	static boolean challenge;


	public static void playSound(String file) {
		try {
			// Load the sound file
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file));

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

	public static boolean isValidAmount(String s) {
		try {
			if (Integer.parseInt(s) > 0)
				return true;
			else
				return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void initGuiObjects() {
		btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		btnNewGame.setBounds(20, 610, 99, 50);
		frame.getContentPane().add(btnNewGame);

		btnEndGame = new JButton("End Game");
		btnEndGame.setEnabled(false);
		btnEndGame.setBounds(121, 610, 99, 50);
		btnEndGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.repaint();
				initGuiObjects();
			}
		});
		frame.getContentPane().add(btnEndGame);

		tfBalance = new JTextField();
		tfBalance.setText("100");
		tfBalance.setBounds(131, 580, 89, 28);
		frame.getContentPane().add(tfBalance);
		tfBalance.setColumns(10);

		lblInitialBalance = new JLabel("Initial Balance:");
		lblInitialBalance.setFont(new Font("Arial", Font.BOLD, 13));
		lblInitialBalance.setForeground(Color.WHITE);
		lblInitialBalance.setBounds(30, 586, 100, 16);
		frame.getContentPane().add(lblInitialBalance);
	}

	public static void showBetGui() {
		btnEndGame.setEnabled(true);

		lblCurrentBalance = new JLabel("Current Balance:");
		lblCurrentBalance.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentBalance.setFont(new Font("Arial", Font.BOLD, 16));
		lblCurrentBalance.setForeground(Color.WHITE);
		lblCurrentBalance.setBounds(315, 578, 272, 22);
		frame.getContentPane().add(lblCurrentBalance);

		lblBalanceAmount = new JLabel();
		lblBalanceAmount.setText(String.format("$%.2f", balance));
		lblBalanceAmount.setForeground(Color.ORANGE);
		lblBalanceAmount.setFont(new Font("Arial", Font.BOLD, 40));
		lblBalanceAmount.setHorizontalAlignment(SwingConstants.CENTER);
		lblBalanceAmount.setBounds(315, 600, 272, 50);
		frame.getContentPane().add(lblBalanceAmount);

		lblInfo = new JLabel("Please enter a bet and click Deal");
		lblInfo.setBackground(Color.ORANGE);
		lblInfo.setOpaque(false);
		lblInfo.setForeground(Color.ORANGE);
		lblInfo.setFont(new Font("Arial", Font.BOLD, 16));
		lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblInfo.setBounds(290, 482, 320, 28);
		frame.getContentPane().add(lblInfo);

		tfBetAmount = new JTextField();
		tfBetAmount.setText("10");
		tfBetAmount.setBounds(790, 580, 89, 28);
		frame.getContentPane().add(tfBetAmount);

		lblEnterBet = new JLabel("Enter Bet:");
		lblEnterBet.setFont(new Font("Arial", Font.BOLD, 14));
		lblEnterBet.setForeground(Color.WHITE);
		lblEnterBet.setBounds(689, 586, 100, 16);
		frame.getContentPane().add(lblEnterBet);

		btnDeal = new JButton("Deal");
		btnDeal.setBounds(679, 610, 200, 50);
		btnDeal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deal();
			}
		});
		frame.getContentPane().add(btnDeal);
		btnDeal.requestFocus();

		// Add UI elements for card swapping
//		playerCardComboBox = new JComboBox<>(new String[]{"Card 1", "Card 2"});
//		playerCardComboBox.setBounds(290, 555, 140, 25);
//		frame.getContentPane().add(playerCardComboBox);
//
//		dealerCardComboBox = new JComboBox<>(new String[]{"Card 1", "Card 2"});
//		dealerCardComboBox.setBounds(470, 555, 140, 25);
//		frame.getContentPane().add(dealerCardComboBox);
//
//		btnSwapCards = new JButton("Swap Cards");
//		btnSwapCards.setBounds(679, 555, 200, 30);
//		btnSwapCards.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				swapCards();
//			}
//		});
//		frame.getContentPane().add(btnSwapCards);
//
//		frame.repaint();
	}

	public static void deal() {
		if (lblShuffleInfo != null)
			frame.getContentPane().remove(lblShuffleInfo);

		dealerCards = new CardGroup();
		playerCards = new CardGroup();

		if (isValidAmount(tfBetAmount.getText())) {
			playSound("coins-handling.wav");
			betAmount = Integer.parseInt(tfBetAmount.getText());
		} else {
			lblInfo.setText("Error: Bet must be a natural number!");
			tfBetAmount.requestFocus();
			return;
		}

		if (betAmount > balance) {
			lblInfo.setText("Error: Bet higher than balance!");
			tfBetAmount.requestFocus();
			return;
		}
		balance -= betAmount;

		lblBalanceAmount.setText(String.format("$%.2f", balance));

		tfBetAmount.setEnabled(false);
		btnDeal.setEnabled(false);

		lblInfo.setText("Please Hit or Stand");

		lblDealer = new JLabel("Dealer");
		lblDealer.setForeground(Color.WHITE);
		lblDealer.setFont(new Font("Arial Black", Font.BOLD, 20));
		lblDealer.setBounds(415, 158, 82, 28);
		frame.getContentPane().add(lblDealer);

		lblPlayer = new JLabel("Player");
		lblPlayer.setForeground(Color.WHITE);
		lblPlayer.setFont(new Font("Arial Black", Font.BOLD, 20));
		lblPlayer.setBounds(415, 266, 82, 28);
		frame.getContentPane().add(lblPlayer);

		btnHit = new JButton("Hit");
		btnHit.setBounds(290, 515, 140, 35);
		btnHit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hit();
			}
		});
		frame.getContentPane().add(btnHit);
		btnHit.requestFocus();

		btnStand = new JButton("Stand");
		btnStand.setBounds(470, 515, 140, 35);
		btnStand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stand();
			}
		});
		frame.getContentPane().add(btnStand);

		btnContinue = new JButton("Continue");
		btnContinue.setEnabled(false);
		btnContinue.setVisible(false);
		btnContinue.setBounds(290, 444, 320, 35);
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				acceptOutcome();
			}
		});
		frame.getContentPane().add(btnContinue);

		lblBetAmount = new JLabel();
		lblBetAmount.setText("$" + betAmount);
		lblBetAmount.setHorizontalAlignment(SwingConstants.CENTER);
		lblBetAmount.setForeground(Color.ORANGE);
		lblBetAmount.setFont(new Font("Arial", Font.BOLD, 40));
		lblBetAmount.setBounds(679, 488, 200, 50);
		frame.getContentPane().add(lblBetAmount);

		lblBetAmountDesc = new JLabel("Bet Amount:");
		lblBetAmountDesc.setHorizontalAlignment(SwingConstants.CENTER);
		lblBetAmountDesc.setForeground(Color.WHITE);
		lblBetAmountDesc.setFont(new Font("Arial", Font.BOLD, 16));
		lblBetAmountDesc.setBounds(689, 465, 190, 22);
		frame.getContentPane().add(lblBetAmountDesc);

		frame.repaint();

		dealerHiddenCard = deck.takeCard();
		dealerCards.cards.add(new Card("", "", 0));
		dealerCards.cards.add(deck.takeCard());

		playerCards.cards.add(deck.takeCard());
		playerCards.cards.add(deck.takeCard());

		updateCardPanels();
		simpleOutcomes();
	}

	private static void hit() {
		playerCards.cards.add(deck.takeCard());
		updateCardPanels();
		simpleOutcomes();
	}

	private static boolean simpleOutcomes() {
		boolean outcomeHasHappened = false;
		int playerScore = playerCards.getTotalValue();
		if (playerScore > 21 && playerCards.getNumAces() > 0)
			playerScore -= 10;

		if (playerScore == 21) {
			dealerCards.cards.set(0, dealerHiddenCard);
			updateCardPanels();
			if (dealerCards.getTotalValue() == 21) {
				lblInfo.setText("Push!");
				balance += betAmount;
			} else {
				lblInfo.setText(String.format("Player gets Blackjack! Profit: $%.2f", 1.5f * betAmount));
				playSound("light-applause.wav");

				balance += 2.5f * betAmount;
			}
			lblBalanceAmount.setText(String.format("$%.2f", balance));
			outcomeHasHappened = true;
			outcomeHappened();
		} else if (playerScore > 21) {
			lblInfo.setText("Player goes Bust! Loss: $" + betAmount);
			playSound("lose.wav");
			dealerCards.cards.set(0, dealerHiddenCard);
			updateCardPanels();
			outcomeHasHappened = true;
			outcomeHappened();
		}
		return outcomeHasHappened;
	}

	private static void stand() {
		if (simpleOutcomes())
			return;

		int playerScore = playerCards.getTotalValue();
		if (playerScore > 21 && playerCards.getNumAces() > 0)
			playerScore -= 10;

		dealerCards.cards.set(0, dealerHiddenCard);

		int dealerScore = dealerCards.getTotalValue();

		while (dealerScore < 16) {
			dealerCards.cards.add(deck.takeCard());
			dealerScore = dealerCards.getTotalValue();
			if (dealerScore > 21 && dealerCards.getNumAces() > 0)
				dealerScore -= 10;
		}
		updateCardPanels();

		if (playerScore > dealerScore) {
			lblInfo.setText("Player wins! Profit: $" + betAmount);
			playSound("cha-ching.wav");

			balance += betAmount * 2;
			lblBalanceAmount.setText(String.format("$%.2f", balance));
		} else if (dealerScore == 21) {
			lblInfo.setText("Dealer gets Blackjack! Loss: $" + betAmount);
			playSound("light-applause.wav");

		} else if (dealerScore > 21) {
			lblInfo.setText("Dealer goes Bust! Profit: $" + betAmount);
			playSound("lose.wav");
			balance += betAmount * 2;
			lblBalanceAmount.setText(String.format("$%.2f", balance));
		} else if (playerScore == dealerScore) {
			lblInfo.setText("Push!");
			balance += betAmount;
			lblBalanceAmount.setText(String.format("$%.2f", balance));
		} else {
			lblInfo.setText("Dealer Wins! Loss: $" + betAmount);
			playSound("cha-ching.wav");

		}
		outcomeHappened();
	}

	private static void outcomeHappened() {
		btnHit.setEnabled(false);
		btnStand.setEnabled(false);
		lblInfo.setOpaque(true);
		lblInfo.setForeground(Color.RED);
		// Replace the existing Timer instantiation
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				btnContinue.setEnabled(true);
				btnContinue.setVisible(true);
				btnContinue.requestFocus();
			}
		}, 500);

	}

	private static void acceptOutcome() {
		lblInfo.setOpaque(false);
		lblInfo.setForeground(Color.ORANGE);
		frame.getContentPane().remove(lblDealer);
		frame.getContentPane().remove(lblPlayer);
		frame.getContentPane().remove(btnHit);
		frame.getContentPane().remove(btnStand);
		frame.getContentPane().remove(lblBetAmount);
		frame.getContentPane().remove(lblBetAmountDesc);
		frame.getContentPane().remove(btnContinue);
		frame.getContentPane().remove(dealerCardPanel);
		frame.getContentPane().remove(playerCardPanel);
		lblInfo.setText("Please enter a bet and click Deal");
		tfBetAmount.setEnabled(true);
		btnDeal.setEnabled(true);
		btnDeal.requestFocus();
		frame.repaint();

		if (balance <= 0) {
			int choice = JOptionPane.showOptionDialog(null,
					"You have run out of funds. Press Yes to add $100, or No to end the current game.",
					"Out of funds", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

			if (choice == JOptionPane.YES_OPTION) {
				balance += 100;
				lblBalanceAmount.setText(String.format("$%.2f", balance));
			} else {
				frame.getContentPane().removeAll();
				frame.repaint();
				initGuiObjects();
				return;
			}
		}

		roundCount++;
		if (roundCount >= 5) {
			deck.initFullDeck();
			deck.shuffle();

			lblShuffleInfo = new JLabel("Deck has been replenished and reshuffled!");
			lblShuffleInfo.setForeground(Color.ORANGE);
			lblShuffleInfo.setFont(new Font("Arial", Font.BOLD, 20));
			lblShuffleInfo.setHorizontalAlignment(SwingConstants.CENTER);
			lblShuffleInfo.setBounds(235, 307, 430, 42);
			frame.getContentPane().add(lblShuffleInfo);

			roundCount = 0;
		}
	}

	public static void setMode(boolean mode) {
		challenge = mode;
	}


	private static void newGame() {
		Object[] options = {"Challenge Mode", "Normal Mode"};
		int choice = JOptionPane.showOptionDialog(frame,
				"Choose your desire mode",
				"Swap Cards Option",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);

		 boolean showSwapCardsButton = (choice == JOptionPane.YES_OPTION);

		 setMode(showSwapCardsButton);

		System.out.println(challenge);

		if (isValidAmount(tfBalance.getText())) {
			balance = Integer.parseInt(tfBalance.getText());
		} else {
			JOptionPane.showMessageDialog(frame, "Invalid balance! Please ensure it is a natural number.",
					"Error", JOptionPane.ERROR_MESSAGE);
			tfBalance.requestFocus();
			return;
		}

		btnNewGame.setEnabled(false);
		tfBalance.setEnabled(false);

		showBetGui();

		roundCount = 0;

		deck = new CardGroup();
		deck.initFullDeck();
		deck.shuffle();

//		System.out.println(showSwapCardsButton);

		if (showSwapCardsButton) {
			// Add UI elements for card swapping
			playerCardComboBox = new JComboBox<>(new String[]{"Card 1", "Card 2"});
			playerCardComboBox.setBounds(290, 555, 140, 25);
			frame.getContentPane().add(playerCardComboBox);

//			dealerCardComboBox = new JComboBox<>(new String[]{"Card 1", "Card 2"});
//			dealerCardComboBox.setBounds(470, 555, 140, 25);
//			frame.getContentPane().add(dealerCardComboBox);

			btnSwapCards = new JButton("Swap Cards");
			btnSwapCards.setBounds(679, 555, 200, 30);
			btnSwapCards.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					swapCards();
				}
			});
			frame.getContentPane().add(btnSwapCards);

			frame.repaint();
		}
	}


	private static void updateCardPanels() {
		if (dealerCardPanel != null) {
			frame.getContentPane().remove(dealerCardPanel);
			frame.getContentPane().remove(playerCardPanel);
		}

		dealerCardPanel = new CardGroupPanel(dealerCards, 420 - (dealerCards.getCount() * 40), 50, 70, 104, 10);
		frame.getContentPane().add(dealerCardPanel);
		playerCardPanel = new CardGroupPanel(playerCards, 420 - (playerCards.getCount() * 40), 300, 70, 104, 10);
		frame.getContentPane().add(playerCardPanel);

		frame.repaint();
	}

	private static void swapCards() {
		int selectedPlayerCardIndex = playerCardComboBox.getSelectedIndex();
		int selectedDealerCardIndex = 1;

		if (selectedPlayerCardIndex >= 0 && selectedPlayerCardIndex < playerCards.getCount() &&
				selectedDealerCardIndex >= 0 && selectedDealerCardIndex < dealerCards.getCount()) {
			Card tempPlayerCard = playerCards.cards.get(selectedPlayerCardIndex);
			Card tempDealerCard = dealerCards.cards.get(selectedDealerCardIndex);

			playerCards.cards.set(selectedPlayerCardIndex, tempDealerCard);
			dealerCards.cards.set(selectedDealerCardIndex, tempPlayerCard);

			updateCardPanels();
		}
	}

	public static void main(String[] args) {
		initGuiObjects();
		frame.setVisible(true);
	}
}

