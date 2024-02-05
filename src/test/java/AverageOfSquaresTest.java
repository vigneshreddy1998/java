import lamdba.AverageOfSquares;
import org.junit.Assert;
import org.junit.Test;

public class AverageOfSquaresTest {
    AverageOfSquares averageOfSquares = new AverageOfSquares();

    @Test
    public void averageTest(){
        int output = averageOfSquares.averageOfSquares(1'3');
        Assert.assertEquals(output);
    }
}
