package chessutils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import com.game.chess.enums.PieceType;

public class ChessUtils {


  public static boolean isValidPromotion(Optional<PieceType> promotion) {
    return promotion.map(p -> PieceType.promotableRoles().contains(p)).orElse(true);
  }
  
  public static int generateGameId() {
    // Get current date and time
    LocalDateTime now = LocalDateTime.now();

    // Extract components from the current date and time
    int year = now.getYear() % 100; // Using the last two digits of the year
    int month = now.getMonthValue();
    int day = now.getDayOfMonth();
    int hour = now.getHour();
    int minute = now.getMinute();
    int second = now.getSecond();

    // Combine components to create a timestamp
    int timestamp = year * 100000000 + month * 1000000 + day * 10000 + hour * 100 + minute * 10 + second;

    // Generate a random value (you can customize the range)
    Random random = new Random();
    int randomValue = random.nextInt(1000); // Adjust the range as needed

    // Combine timestamp and random value to create the final game ID
    int gameId = timestamp * 1000 + randomValue;

    return gameId;
}


}
