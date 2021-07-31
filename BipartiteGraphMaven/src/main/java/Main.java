import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Main {

    static List<Edge> edges = new ArrayList<>();

    public static void main(String[] args) {
        readXLSXFile();
        Graph graph = new Graph(edges);
        int x = 1;
    }

    static void readXLSXFile() {
        FileInputStream fileInputStream = null;
        XSSFWorkbook workbook = null;
        try
        {
            fileInputStream = new FileInputStream("forks.xlsx");
            workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();
            Row row;

            while(iterator.hasNext())
            {
                row = iterator.next();
                if(row.getRowNum() == 0)
                    continue;
                CreateEdges(row);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if(fileInputStream != null)
                    fileInputStream.close();
                if(workbook != null)
                    workbook.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }

        }

    }

    private static void CreateEdges(Row currentRow) {
        Iterator<Cell> cellIterator = currentRow.cellIterator();
        //
        // Cells are ordered as: repo id,
        //                       repo api link,
        //                       id(s) of the repo commiter(s)
        //
        while(cellIterator.hasNext())
        {
            String repoId = GetMemberValue(cellIterator.next());
            String apiLink = GetMemberValue(cellIterator.next());
            //boolean isIndependent = GetMemberValue(cellIterator.next()).equalsIgnoreCase("yes");
           // String committerId = GetMemberValue(cellIterator.next());
           // cellIterator.next();

            String numCommitterStr = "";
            try {
                numCommitterStr = GetMemberValue(cellIterator.next()).replace(".0", "");
            } catch (NoSuchElementException ex) {}
            Integer numberOfCommitters = numCommitterStr.isEmpty() ? 0 : Integer.parseInt(numCommitterStr);

            String numForksStr = "";
            try {
                numForksStr = GetMemberValue(cellIterator.next()).replace(".0", "");
            } catch (NoSuchElementException ex) {}
            Integer numberOfForks = numForksStr.isEmpty() ? 0 : Integer.parseInt(numForksStr);

//            String[] committerSplit = CommitterStringToArray(committerId);

            Repository repo = new Repository(repoId, apiLink, numberOfCommitters, false, numberOfForks);
            if(numberOfCommitters == 0)
            {
                Committer committer = new Committer("None");
                edges.add(new Edge(committer, repo));
            }
            else
            {
                for(Integer i = 0; i < numberOfCommitters; i++)
                {
                    edges.add(new Edge(new Committer(i.toString()), repo));
                }
//                for(String cId : committerSplit)
//                {
//                    edges.add(new Edge(new Committer(cId), repo));
//                }
            }
        }
    }

    static String[] CommitterStringToArray(String commiters) {
        if(commiters.isEmpty()) return  null;
        return commiters.replaceAll("\\s+", "").split(",");
    }

    static String GetMemberValue(Cell cell){
        String toReturn;

        if(cell.getCellType() == CellType.STRING)
            toReturn = cell.getStringCellValue();
        else
            toReturn = Double.toString(cell.getNumericCellValue());

        return  toReturn;
    }
}
