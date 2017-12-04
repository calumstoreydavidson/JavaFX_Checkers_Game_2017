/**
 * represents a mildly humorous explanation for invalid move errors - subtly inspired by portal
 */
public enum InvalidMoveError {

    //when the user try's to put a unit outside of the board
    OUTSIDE_BOARD_ERROR("\nIt appears that you tried to move beyond the bounds of the game board. " +
                          "\n- Do you throw you're pieces off the board in real life?" +
                          "\n- In any case, if you're really that desperate to play over there, " +
                          "I suppose you could just move the window?\n\n"),

    SAME_POSITION_ERROR("\nYou might have more success if you actually move something?" +
                           "\n- Tell you what though, why don't you try that again in god mode, have fun.\n\n"),

    //when the user try's to put a unit on another unit
    TILE_ALREADY_OCCUPIED_ERROR("\nIt seems that spot is already occupied by another unit. " +
                                   "\n- Surely it can't have offended you that badly?, I hear he's actually quite nice. " +
                                   "\n- But if its really bothering you that much, perhaps you should just use god mode to " +
                                   "dispose of it?\n\n"),

    //when a user try's to put a unit on the wrong colour tile
    NOT_PLAY_SQUARE_ERROR("\nIt seems that spot is already occupied by another unit. " +
                             "\n- Surely it can't have offended you that badly?, I hear he's actually quite nice. " +
                             "\n- But if its really bothering you that much, perhaps you should just use god mode to " +
                             "dispose of it?\n\n"),

    //when a user try's to put a unit somewhere is can't reach, or if they figure out how to break the game some other way
    DISTANT_MOVE_AND_CATCHALL_ERROR("\nYou probably just tried to move to some distant square - you have played this game before right?" +
                                       "\n- If, somehow, that's not what you did and your seeing this message anyway, congratulations " +
                                       "you found an error we didn't think of, you should be proud of your ability to break things.\n\n");

    //an explanation for why the move was invalid
    private final String explanation;

    /**
     * create a new invalidMoveError with an error explanation
     *
     * @param explanation an explanation for why the move was invalid
     */
    InvalidMoveError(String explanation) {
        this.explanation = explanation;
    }

    /**
     * get an explanation for why the move was invalid
     *
     * @return an explanation for why the move was invalid
     */
    public String getExplanation() {
        return explanation;
    }
}