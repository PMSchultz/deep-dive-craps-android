package edu.cnm.deepdive.games.crapssimulator;

import android.content.Context;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//class that knows how to keep track of a craps game
public class Game {

  private Context context;


  private Random rng;
  private State state;
  private int point;
  private long plays;
  private long wins;
  private List<Roll> rolls;

  //game constructor with initial values
  public Game(Context context) {
    this.context = context;
    plays = 0;
    wins = 0;
    rolls = new LinkedList<>();
    reset();
  }
//getters and one setter
  public State getState() {
    return state;
  }

  public int getPoint() {
    return point;
  }

  public long getPlays() {
    return plays;
  }

  public long getWins() {
    return wins;
  }

  public long getLosses(){
    return plays - wins;
  }

  public List<Roll> getRolls() {
    return rolls;
  }

  public void setRng(Random rng) {
    this.rng = rng;
  }

  //roll the dice in a single play
  public void roll() {
    if (state != State.COME_OUT && state != State.POINT) {
      return;
    }
    Roll roll = (state == State.POINT) ? new Roll(point) : new Roll();
    if (point == 0 && roll.after == State.POINT) {
      point = roll.dice[0] + roll.dice[1];
    }
    state = roll.after;
    rolls.add(roll);
    if(state == State.WIN || state == State.LOSE) {
      plays++;
      if (state == State.WIN) {
        wins++;
      }
    }

  }
  //how to advance an entire play

  public void play(){
    while (state != State.WIN && state != State.LOSE){
      roll();
    }
  }
  //clears the list
  public void reset(){
    state = State.COME_OUT;
    point = 0;
    rolls.clear();
  }

//a record/transcript of what is happening
  public class Roll{

    public final State before;
    public final State after;
    public final int[] dice = {
        rng.nextInt(6) + 1,
        rng.nextInt(6) + 1
    };

    //constructor that initializes finals
    private Roll(){
      before = State.COME_OUT;
      after = before.newState(dice[0]+ dice[1]);

      }
      //constructor for rolls for Point
    private Roll(int point){
    before = State.POINT;
    after = before.newState(dice[0]+ dice[1],point);


    }

  @Override
  public String toString() {
    return String.format(
        context.getString(R.string.dice_roll_message),
        before, Arrays.toString(dice), after);
  }
}

  public enum State {
    COME_OUT {
      @Override
      public State newState(int roll) {
        switch (roll) {
          case 7:
          case 11:
            return WIN;
          case 2:
          case 3:
          case 12:
            return LOSE;
          default:
            return POINT;
        }
      }


      /**
       * Computes and returns next state instance, based on this instance and
       * roll and point values
       * @param roll
       * @param point
       * @return
       */
      public State newState(int roll, int point) {
        return newState(roll);
      }
    },

    POINT {
      @Override
      /**
       * Computes and returns next state instance, based on this instance and
       * roll values
       * @param roll
       * @return
       * @throws IllegalArgumentException
       */
      public State newState(int roll)
          throws IllegalArgumentException {
        if (roll != 7) {
          throw new IllegalArgumentException();
        }
        return LOSE;

      }

      @Override
      public State newState(int roll, int point) {
        if (roll == point) {
          return WIN;
        } else if (roll == 7) {
          return LOSE;
        }
        return POINT;
      }
    },


    WIN {},


    LOSE {};

    public State newState(int roll) {
      return this;

    }

    public State newState(int roll, int point) {
      return this;
    }

  }



}
