package com.pokebattler.fight.data;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.pokebattler.fight.data.proto.Cpm.CpM;

@Repository
public class CpMRepository {
    Map<String,CpM> cpMap;
    Logger log = LoggerFactory.getLogger(getClass());
    public CpMRepository () {
        Map<String,CpM> tempMap = new HashMap<>();
        try (InputStream is =  this.getClass().getResourceAsStream("cpm.csv");Scanner lineScanner = new Scanner(is)) {
            while(lineScanner.hasNext()) {
                String s = lineScanner.next();
                try (Scanner rowScanner = new Scanner(s);) {
                    Scanner r = rowScanner.useDelimiter(",");
                    String levelS= r.next();
                    String[] level = levelS.split("\\.");
                    CpM cpm = CpM.newBuilder()
                        .setLevel(Integer.parseInt(level[0]))
                        .setHalfLevel(level.length == 2)
                        .setCpm(r.nextDouble())
                        .setCandies(r.nextInt()).build();                        
                    tempMap.put(levelS, cpm);
                }
            }
        } catch (Exception e) {
            log.error("Could not initialize resists",e);
            throw new IllegalArgumentException("resists.csv is not valid");
        }
        cpMap = Collections.unmodifiableMap(tempMap);
        log.info("Loaded {} cpms",cpMap.size());
    }
    public CpM getCpM(String level) {
        return cpMap.get(level);
    }
}
