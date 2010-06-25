package api;

import au.com.bytecode.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Csv {
    private List<Map<String, String>> rows = new ArrayList();

    public Csv(InputStream input) {
        CSVReader reader = new CSVReader(new InputStreamReader(input));

        try {
            String[] header = reader.readNext();
            String[] row;

            while(null != (row = reader.readNext())) {
                addRow(header, row);
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addRow(String[] header, String[] data) {
        HashMap<String, String> row = new HashMap<String, String>();

        for(int i = 0; i < header.length; i++) {
            row.put(header[i].trim(), data[i].trim());
        }

        rows.add(row);
    }

    public int size() {
        return rows.size();
    }

    public Map<String, String> row(int index) {
        return rows.get(index);
    }
}
