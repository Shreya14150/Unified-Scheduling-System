package main;

import scheduling.Match;
import java.util.*;

public class SportsMatchScheduler {
    private List<Queue<Match>> rounds;
    private Set<String> matchHistory;
    private List<String> currentTeams;  // stores the current teams

    public SportsMatchScheduler() {
        rounds = new ArrayList<>();
        matchHistory = new HashSet<>();
        currentTeams = new ArrayList<>();
    }

    public void scheduleRoundRobin(List<String> teams) {
        System.out.println("Scheduling Round Robin Matches:");
        if (teams.size() % 2 != 0) {
            teams.add("BYE");
        }

        int numTeams = teams.size();
        int numRounds = numTeams - 1;
        int half = numTeams / 2;

        List<String> rotatedTeams = new ArrayList<>(teams);

        for (int round = 0; round < numRounds; round++) {
            Queue<Match> roundMatches = new LinkedList<>();
            for (int i = 0; i < half; i++) {
                String teamA = rotatedTeams.get(i);
                String teamB = rotatedTeams.get(numTeams - 1 - i);

                if (!teamA.equals("BYE") && !teamB.equals("BYE")) {
                    String matchKey = teamA + " vs " + teamB;
                    if (!matchHistory.contains(matchKey) && !matchHistory.contains(teamB + " vs " + teamA)) {
                        matchHistory.add(matchKey);
                        Match match = new Match(teamA, teamB, "Round " + (round + 1));
                        roundMatches.add(match);
                    }
                }
            }

            rounds.add(roundMatches);

            // Rotate teams except first
            List<String> newRotation = new ArrayList<>();
            newRotation.add(rotatedTeams.get(0));
            newRotation.add(rotatedTeams.get(numTeams - 1));
            for (int i = 1; i < numTeams - 1; i++) {
                newRotation.add(rotatedTeams.get(i));
            }
            rotatedTeams = newRotation;
        }

        printSchedule();
    }

    public void scheduleKnockout(List<String> teams) {
        System.out.println("Scheduling Knockout Matches:");
        Queue<Match> matches = new LinkedList<>();

        for (int i = 0; i < teams.size(); i += 2) {
            if (i + 1 < teams.size()) {
                Match match = new Match(teams.get(i), teams.get(i + 1), "Knockout Round");
                matches.add(match);
            } else {
                System.out.println(teams.get(i) + " advances (no opponent)");
            }
        }

        rounds.add(matches);
        printSchedule();
    }

    private void printSchedule() {
        for (int i = 0; i < rounds.size(); i++) {
            System.out.println("Round " + (i + 1) + ":");
            for (Match match : rounds.get(i)) {
                System.out.println(" - " + match.getTeamA() + " vs " + match.getTeamB());
            }
        }
    }

    public void run(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Sports Match Scheduler ---");
            System.out.println("1. Schedule Matches");
            System.out.println("2. Schedule Knockout");
            System.out.println("3. Clear Schedule");
            System.out.println("0. Back to Main Menu");

            int option = getValidInt(scanner, "Enter your choice: ");

            if (option == 1) {
                if (!rounds.isEmpty()) {
                    System.out.print("Previous schedule exists. Clear it before proceeding? (yes/no): ");
                    String ans = scanner.nextLine().trim().toLowerCase();
                    if (ans.equals("yes")) {
                        rounds.clear();
                        matchHistory.clear();
                        System.out.println("Previous schedule cleared.");
                    } else {
                        System.out.println("Returning to menu...");
                        continue;
                    }
                }

                int n = getValidInt(scanner, "Enter number of teams: ");

                if (n <= 1) {
                    System.out.println("At least 2 teams are required.");
                    continue;
                }

                Set<String> teamSet = new HashSet<>();
                List<String> teams = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    System.out.print("Enter name for team " + (i + 1) + ": ");
                    String name = scanner.nextLine().trim();

                    if (name.isEmpty() || !name.matches("[A-Za-z0-9 _-]+")) {
                        System.out.println("Invalid name. Use letters, numbers, spaces, underscores or hyphens.");
                        i--;
                        continue;
                    }

                    if (teamSet.contains(name)) {
                        System.out.println("Duplicate name. Enter a unique team name.");
                        i--;
                        continue;
                    }

                    teamSet.add(name);
                    teams.add(name);
                }

                currentTeams.clear();  // ✅ Save teams globally
                currentTeams.addAll(teams);

                scheduleRoundRobin(teams);

            } else if (option == 2) {
                if (!rounds.isEmpty()) {
                    System.out.print("Previous schedule exists. Clear it before proceeding? (yes/no): ");
                    String ans = scanner.nextLine().trim().toLowerCase();
                    if (ans.equals("yes")) {
                        rounds.clear();
                        matchHistory.clear();
                        System.out.println("Previous schedule cleared.");
                    } else {
                        System.out.println("Returning to menu...");
                        continue;
                    }
                }

                if (currentTeams.isEmpty()) {
                    System.out.println("No team list found. Please schedule matches first or enter teams.");
                    continue;
                }

                scheduleKnockout(currentTeams);

            } else if (option == 3) {
                rounds.clear();
                matchHistory.clear();
                System.out.println("Schedule cleared.");
            } else if (option == 0) {
                back = true;
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }
    }

    private int getValidInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // clear newline
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.nextLine(); // clear invalid input
            }
        }
    }
}
