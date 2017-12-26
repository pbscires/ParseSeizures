import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ParseSeizures {
    public static void main(String[] args)
    {
        JSONObject rootObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");

        String root = "D:\\Documents\\database_mit\\";
        for(int i=1;i<24;i++)
        {
            String suffix = "chb" + (i<10 ? ("0"+i) : i);
            stringBuilder.append(String.format("'%s' : [\n", suffix));
            String path = root + suffix + "\\" + suffix + "-summary.txt";
            System.out.println(path);

            JSONArray jsonSeizuresArr = new JSONArray();
            try
            {
                FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = bufferedReader.readLine();

                while(line != null)
                {
                    if (line.length() < 8)
                    {
                        line = bufferedReader.readLine();
                        continue;
                    }
                    if(line.substring(0,9).equals("File Name"))
                    {
                        String fileName = line.substring(11);
                        String startTime = bufferedReader.readLine().substring(17);
                        String endTime = bufferedReader.readLine().substring(15);
                        int numSeizures = Integer.parseInt(bufferedReader.readLine().substring(28));

                        System.out.println(fileName + "\n" + startTime + "\n" + endTime + "\n" + numSeizures);

                        if (numSeizures > 0)
                        {
                            JSONObject jsonSeizureObj = new JSONObject();
                            jsonSeizureObj.put("FileName", fileName);
                            jsonSeizureObj.put("StartTime", startTime);
                            jsonSeizureObj.put("EndTime", endTime);
                            jsonSeizureObj.put("NumSeizures", numSeizures);
                            ArrayList<Integer> seizureStart = new ArrayList<Integer>();
                            ArrayList<Integer> seizureEnd = new ArrayList<Integer>();
                            line = bufferedReader.readLine();
                            while (line!=null && !line.equals(""))
                            {
                                seizureStart.add(Integer.parseInt(line.substring(((numSeizures>1) ? 22 : 20)).replace(" seconds", "")));
                                line = bufferedReader.readLine();
                                seizureEnd.add(Integer.parseInt(line.substring(((numSeizures>1) ? 20 : 18)).replace(" seconds", "")));
                                line = bufferedReader.readLine();
                            }
                            JSONArray jsonSeizureStartTimes = new JSONArray();
                            JSONArray jsonSeizureEndTimes = new JSONArray();
                            for (int j=0;j<seizureStart.size();j++)
                            {
                                jsonSeizureStartTimes.add(seizureStart.get(j));
                                jsonSeizureEndTimes.add(seizureEnd.get(j));
                            }
                            jsonSeizureObj.put("SeizureStartTimes", jsonSeizureStartTimes);
                            jsonSeizureObj.put("SeizureEndTimes", jsonSeizureEndTimes);
                            jsonSeizuresArr.add(jsonSeizureObj);
                            String seizureStartString = "[" + seizureStart.get(0);
                            String seizureEndString = "[" + seizureEnd.get(0);
                            for (int j=1;j<seizureStart.size();j++)
                            {
                                seizureStartString += "," + seizureStart.get(j);
                                seizureEndString += "," + seizureEnd.get(j);
                            }

                            seizureStartString +="]";
                            seizureEndString+="]";

                            String lineToAppend = String.format("{'FileName' : '%s', 'StartTime' : '%s', 'EndTime' : '%s', 'SeizureStartTime' : %s, 'SeizureEndTime' : %s},\n",
                                    fileName, startTime, endTime, seizureStartString, seizureEndString);
                            stringBuilder.append(lineToAppend);
                        }
                    }
                    line = bufferedReader.readLine();
                }
                Scanner in = new Scanner(System.in);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            stringBuilder.append("],\n");
            rootObj.put(suffix, jsonSeizuresArr);
        }
        stringBuilder.append("}");
        System.out.println(stringBuilder);
        File file = new File("D:\\Documents\\python_workspace\\eegAnalysis\\Configuration\\seizures.json");

        try
        {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(rootObj.toJSONString());
            fileWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
