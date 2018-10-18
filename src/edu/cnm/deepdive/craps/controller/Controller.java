package edu.cnm.deepdive.craps.controller;

import edu.cnm.deepdive.craps.model.Game;
import edu.cnm.deepdive.craps.model.Game.Roll;
import edu.cnm.deepdive.craps.model.Game.State;
import edu.cnm.deepdive.craps.view.RollCell;
import java.security.SecureRandom;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class Controller {

  private static final String PLACE_HOLDER_STYLE_CLASS = "";
  private static final String WIN_STYLE_CLASS = "win";
  private static final String LOSS_STYLE_CLASS = "loss";

  private Game game;
  private boolean running;
  private String tallyFormat;

  @FXML
  private ResourceBundle resources;
  @FXML
  private MenuItem next;
  @FXML
  private MenuItem fast;
  @FXML
  private MenuItem pause;
  @FXML
  private MenuItem reset;
  @FXML
  private ListView<Roll> rolls;
  @FXML
  private Text tally;


  @FXML
  private void initialize() {
    tallyFormat = tally.getText();
    rolls.setCellFactory(listview -> new RollCell(resources));
    rolls.getStyleClass().add(PLACE_HOLDER_STYLE_CLASS);
  reset(null);
  updateMenu();
}

  @FXML
  private void next(ActionEvent actionEvent) {
    game.play();
    updateTally(game.getWins(), game.getLosses());
    updateRolls(game.getRolls());
  }

  @FXML
  private void fast(ActionEvent actionEvent) {
    running = true;
    updateMenu();
    new Runner().start();
  }

  @FXML
  private void pause(ActionEvent actionEvent) {
    running = false;
  }

  @FXML
  private void reset(ActionEvent actionEvent) {
    game = new Game(new SecureRandom());
    updateTally(game.getWins(), game.getLosses());
    updateRolls(game.getRolls());
  }

  public void stop() {
    pause(null);
  }

  private void updateTally(int wins, int losses) {
    int total = wins + losses;
    double percentage = (total != 0) ? (100.0 * wins / total) : 0;
    tally.setText(String.format(tallyFormat, wins, total, percentage));
  }
  private void updateRolls(List<Roll> diceRolls) {
    List<String>  styleClasses = rolls.getStyleClass();
    styleClasses.remove(styleClasses.size() - 1);
    rolls.getItems().clear();
    if (!diceRolls.isEmpty()) {
      State state = diceRolls.get(diceRolls.size() - 1).getState();
      if (state == State.WIN) {
        styleClasses.add(WIN_STYLE_CLASS);
      } else {
        styleClasses.add(LOSS_STYLE_CLASS);
      }
      rolls.getItems().addAll(diceRolls);
    } else {
      styleClasses.add(PLACE_HOLDER_STYLE_CLASS);
    }

  }

  private void  updateMenu() {
    next.setDisable(running);
    fast.setDisable(running);
    pause.setDisable(!running);
    next.setDisable(running);
  }

  private class Runner extends Thread {

    private static final int TALLY_UPDATE_INTERVAL = 2_000;
    private static final int ROLLS_UPDATE_INTERVAL = 10_000;

    @Override
    public void run() {

      int count = 0;
      while (running) {
        game.play();
        count++;
        if (count % TALLY_UPDATE_INTERVAL == 0) {
          int wins = game.getWins();
          int losses = game.getLosses();
          Platform.runLater(() -> updateTally(wins, losses));
        }
        if (count % ROLLS_UPDATE_INTERVAL == 0) {
          List<Roll> rolls = game.getRolls();
          Platform.runLater(() -> updateRolls(rolls));
        }
      }
      int wins = game.getWins();
      int losses = game.getLosses();
      List<Roll> rolls = game.getRolls();
      Platform.runLater(() -> {
        updateTally(wins, losses);
        updateRolls(rolls);
        updateMenu();
      });
    }
  }

}
