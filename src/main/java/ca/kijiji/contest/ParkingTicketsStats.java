package ca.kijiji.contest;

import clojure.lang.RT;
import clojure.lang.Var;

import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;

public class ParkingTicketsStats {

    public static SortedMap<String, Integer> sortStreetsByProfitability(InputStream parkingTicketsStream) {
        try {
            RT.loadResourceScript("ca/kijiji/contest/parking_tickets_stats.clj");
        } catch (IOException e) {
            throw new IllegalStateException("missing clojure file", e);
        }
        Var function = RT.var("ca.kijiji.contest.parking-tickets-stats", "streets-by-profitability");
        return (SortedMap<String, Integer>) function.invoke(parkingTicketsStream);
    }
}