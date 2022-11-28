import edu.duke.*;
import org.apache.commons.csv.*;
import java.io.*;

/**
* 
* @author: Amir Armion 
* 
* @version: V.01
* 
*/
public class BabyBirths 
{

    public void printNames()
    {
        FileResource fr     = new FileResource();
        CSVParser    parser = fr.getCSVParser(false); // false means this parser(table) doesn't have header

        int numBorn = 0;

        for(CSVRecord record: parser)
        {
            numBorn = Integer.parseInt(record.get(2));

            if(numBorn <= 100)
            {
                System.out.println("Name: " + record.get(0) + ", Gender: " + record.get(1) + ", Num Born: " + record.get(2));
            }
        }    
    }   

    public void totalBirths(FileResource file)
    {
        CSVParser parser = file.getCSVParser(false);

        int totalBirths        = 0;
        int totalBirthsOfGirls = 0;
        int totalBirthsOfBoys  = 0;
        
        int countGirlsName = 0;
        int countBoysName  = 0;

        for(CSVRecord record: parser)
        {
            if(record.get(1).equals("F"))
            {
                totalBirthsOfGirls += Integer.parseInt(record.get(2));
                countGirlsName++;
            }
            else
            {
                totalBirthsOfBoys += Integer.parseInt(record.get(2));
                countBoysName++;
            }

            totalBirths += Integer.parseInt(record.get(2));
        }

        System.out.println("Total births is: " + totalBirths + "\nTotal births of girls is: " + totalBirthsOfGirls + "\nand total births of boys is: " + totalBirthsOfBoys);
        System.out.println("\nNumber of girls name is: " + countGirlsName + "\nNumber of boys name is: " + countBoysName);
    }

    public int getRank(int year, String name, String gender)
    {
        String       fileName = "data/us_babynames_by_year/yob" + year + ".csv";
        FileResource fr       = new FileResource(fileName);
        CSVParser    parser   = fr.getCSVParser(false);

        int femaleRank = 1;
        int maleRank   = 1;
        int rank       = -1;

        for(CSVRecord record: parser)
        {
            String babyName     = record.get(0);
            String genderStatus = record.get(1);

            if(genderStatus.equals("F"))
            {
                if(babyName.equals(name) && genderStatus.equals(gender))
                {
                    rank = femaleRank;
                }

                femaleRank++;
            }
            else
            {
                if(babyName.equals(name) && genderStatus.equals(gender))
                {
                    rank = maleRank;
                }

                maleRank++;
            }
        }

        return rank;
    }

    public String getName(int year, int rank, String gender)
    {
        String       fileName = "data/us_babynames_by_year/yob" + year + ".csv";
        FileResource fr       = new FileResource(fileName);
        CSVParser    parser   = fr.getCSVParser(false);

        int    counter = 1;
        String name    = "NO NAME!";

        for(CSVRecord record: parser)
        {
            String babyName     = record.get(0);
            String genderStatus = record.get(1);

            if(genderStatus.equals(gender))
            {
                if(counter == rank)
                {
                    name = babyName;
                    break;
                }
                else
                {
                    counter++;
                }           
            }
        }

        return name;
    }

    public String whatIsNameInYear(String name, int year, int newYear, String gender)
    {
        int firstRank  = getRank(year, name, gender);

        String newName = getName(newYear, firstRank, gender);

        return newName;
    }

    public int yearOfHighestRank(String name, String gender)
    {
        DirectoryResource dr = new DirectoryResource();

        int highestRank     = 1000000; // Picked a random big number as a Highest Rank
        int highestRankYear = -1;

        for(File f: dr.selectedFiles())
        {
            int year = Integer.parseInt(f.getName().substring(3,7)); 
            System.out.println(year);
            int rank = getRank(year, name, gender);
            System.out.println("Rank: " + rank);
            
            if((rank != -1) && (rank < highestRank))
            {
                highestRank     = rank;
                highestRankYear = year;
            }
        }

        return highestRankYear;
    }

    public double getAverageRank(String name, String gender)
    {
        DirectoryResource dr = new DirectoryResource();

        double averageRank = -1.0;
        double totalRank   = 0.0;
        int    counter     = 0;

        for(File f: dr.selectedFiles())
        {
            int    year     = Integer.parseInt(f.getName().substring(3,7)); 
            int    rank     = getRank(year, name, gender);
            String babyName = getName(year, rank, gender);

            if(name.equals(babyName))
            {
                totalRank += rank;
                counter++;  
            }
        }

        if(counter == 0)
        {
            averageRank = -1.0;
        }
        else
        {
            averageRank = totalRank / counter;
        }

        return averageRank;
    }

    public int getTotalBirthsRankedHigher(int year, String name, String gender)
    {
        String       fileName = "data/us_babynames_by_year/yob" + year + ".csv";
        FileResource fr       = new FileResource(fileName);
        CSVParser    parser   = fr.getCSVParser(false);

        int rank = getRank(year, name, gender);
        int totalNumberOfBirths = 0;

        for(CSVRecord record: parser)
        {
            String babyName      = record.get(0);
            String genderStatus  = record.get(1);
            int    numberOfBirth = Integer.parseInt(record.get(2));

            int newRank = getRank(year, babyName, genderStatus);

            if(genderStatus.equals(gender))
            {
                if(newRank < rank)
                {
                    totalNumberOfBirths += numberOfBirth;
                }
            }
            else
            {
                continue;
            }

            if((genderStatus.equals(gender)) && (newRank >= rank))
            {
                break;
            }
        }

        return totalNumberOfBirths;
    }

    public void testTotalBirths()
    {
        FileResource file = new FileResource("data/us_babynames_by_year/yob1905.csv");

        totalBirths(file);
    }

    public void testGetRank()
    {
        System.out.println("The Rank for name is: " + getRank(1971, "Frank", "M"));
    }

    public void testGetName()
    {
        System.out.println("The name for this rank is " + getName(1982, 450 , "M"));
    }

    public void testWhatIsNameInYear()
    {
        System.out.println("Owen is born in 1974 would be " + whatIsNameInYear("Owen", 1974, 2014, "M") + " if he was born in 2014!");
    }

    public void testYearOfHighestRank()
    {
        System.out.println("The year of highest rank is: " + yearOfHighestRank("Mich", "M"));
    }

    public void testGetAverageRank()
    {
        System.out.println("The average for these files is: " + getAverageRank("Robert", "M"));
    }

    public void testGetTotalBirthsRankedHigher()
    {
        System.out.println("The total number of the births is: " + getTotalBirthsRankedHigher(1990, "Samantha", "F"));
    }
}
