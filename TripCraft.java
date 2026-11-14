import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.*;

// One activity in the trip schedule.
class Activity {
    String name;
    LocalDate date;
    LocalTime startTime, endTime;
    double plannedCost, actualCost;
    String paidBy;

    Activity(String name, LocalDate date, LocalTime startTime,
             LocalTime endTime, double plannedCost, String paidBy) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.plannedCost = plannedCost;
        this.actualCost = 0.0;
        this.paidBy = paidBy;
    }
}

// Stores the trip details and its activities.
class Trip {
    String tripName, destination;
    LocalDate startDate, endDate;
    double budget;
    ArrayList<String> members;
    ArrayList<Activity> activities;

    Trip(String tripName, String destination, LocalDate startDate,
         LocalDate endDate, double budget, ArrayList<String> members) {
        this.tripName = tripName;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.members = members;
        this.activities = new ArrayList<>();
    }
}

public class TripCraft {
    static Scanner sc = new Scanner(System.in);
    static Trip trip;

    // Four destinations with three sample activity suggestions and prices.
    static final String[] DESTINATIONS = {"Goa", "Manali", "Jaipur", "Mysuru"};
    static final LinkedHashMap<String, LinkedHashMap<String, Double>> SUGGESTIONS =
            new LinkedHashMap<>();

    static {
        addSuggestions("Goa", "Dolphin Boat Ride", 800,
                "Fort Aguada Visit", 100, "Water Sports Package", 1200);
        addSuggestions("Manali", "Solang Valley Adventure", 900,
                "Hadimba Temple Visit", 50, "River Rafting", 700);
        addSuggestions("Jaipur", "Amber Fort Visit", 200,
                "City Palace Visit", 300, "Jaipur Food Walk", 500);
        addSuggestions("Mysuru", "Mysore Palace Visit", 100,
                "Chamundi Hills Excursion", 250, "Brindavan Gardens Visit", 100);
    }

    static void addSuggestions(String place, String a1, double p1,
                               String a2, double p2, String a3, double p3) {
        LinkedHashMap<String, Double> activities = new LinkedHashMap<>();
        activities.put(a1, p1);
        activities.put(a2, p2);
        activities.put(a3, p3);
        SUGGESTIONS.put(place, activities);
    }

    static void line() {
        System.out.println("================================================================================");
    }

    static void heading(String title) {
        System.out.println();
        line();
        System.out.println("                         " + title);
        line();
    }

    static void success(String message) { System.out.println("\n[SUCCESS] " + message); }
    static void error(String message) { System.out.println("\n[ERROR] " + message); }
    static String money(double amount) {
        return String.format(Locale.US, "Rs. %,.2f", amount);
    }

    static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) return value;
            error("This field cannot be empty.");
        }
    }

    static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                error("Please enter a valid whole number.");
            }
        }
    }

    static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(sc.nextLine().trim());
                if (Double.isFinite(value) && value >= 0) return value;
                error("Enter a valid non-negative amount.");
            } catch (NumberFormatException e) {
                error("Please enter a valid amount.");
            }
        }
    }

    static LocalDate readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                error("Use YYYY-MM-DD format. Example: 2026-12-10");
            }
        }
    }

    static LocalTime readTime(String message) {

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

    while (true) {
        try {
            System.out.print(message);

            return LocalTime.parse(
                sc.nextLine().trim().toUpperCase(),
                formatter
            );

        } catch (DateTimeParseException e) {

            error("Use AM/PM format. Example: 09:30 AM");
        }
    }
}
    static String formatTime(LocalTime time) {
        return time.format(
            DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
        );
    }
    static boolean tripExists() {
        if (trip == null) {
            error("Please create a trip first!");
            return false;
        }
        return true;
    }

    static String chooseMember(String prompt) {
        while (true) {
            System.out.println("\nTrip members:");
            for (int i = 0; i < trip.members.size(); i++)
                System.out.println((i + 1) + ". " + trip.members.get(i));
            int choice = readInt(prompt);
            if (choice >= 1 && choice <= trip.members.size())
                return trip.members.get(choice - 1);
            error("Select a valid member number.");
        }
    }

    static double getPlannedCost() {
        double total = 0;
        for (Activity a : trip.activities) total += a.plannedCost;
        return total;
    }

    static double getActualCost() {
        double total = 0;
        for (Activity a : trip.activities) total += a.actualCost;
        return total;
    }

    static double getPaidBy(String member) {
        double total = 0;
        for (Activity a : trip.activities)
            if (a.paidBy.equals(member)) total += a.actualCost;
        return total;
    }

    // 1. Create trip and select a destination.
    static void createTrip() {
        heading("CREATE YOUR TRIP");
        if (trip != null) {
            error("A trip already exists in this session.");
            return;
        }

        String name = readText("Enter Trip Name: ");
        LocalDate start = readDate("Enter Start Date (YYYY-MM-DD): ");
        LocalDate end = readDate("Enter End Date (YYYY-MM-DD): ");
        if (end.isBefore(start)) {
            error("End date cannot be before start date.");
            return;
        }

        double budget;
        do {
            budget = readDouble("Enter Total Budget (Rs.): ");
            if (budget <= 0) error("Budget must be greater than zero.");
        } while (budget <= 0);

        System.out.println("\nChoose your trip destination:");
        for (int i = 0; i < DESTINATIONS.length; i++)
            System.out.println((i + 1) + ". " + DESTINATIONS[i]);

        int destinationChoice;
        do {
            destinationChoice = readInt("Enter destination number: ");
            if (destinationChoice < 1 || destinationChoice > DESTINATIONS.length)
                error("Choose a destination from 1 to " + DESTINATIONS.length + ".");
        } while (destinationChoice < 1 || destinationChoice > DESTINATIONS.length);
        String destination = DESTINATIONS[destinationChoice - 1];

        int count;
        do {
            count = readInt("Enter Number of Members: ");
            if (count <= 0) error("At least one member is required.");
        } while (count <= 0);

        ArrayList<String> members = new ArrayList<>();
        System.out.println("\nEnter Member Details:");
        for (int i = 1; i <= count; i++) {
            String member;
            while (true) {
                member = readText("Member " + i + " Name: ");
                boolean duplicate = false;
                for (String existing : members)
                    if (existing.equalsIgnoreCase(member)) duplicate = true;
                if (!duplicate) break;
                error("Member names must be unique.");
            }
            members.add(member);
        }

        trip = new Trip(name, destination, start, end, budget, members);
        success("Trip created successfully!");
        System.out.println("Trip Name    : " + trip.tripName);
        System.out.println("Destination  : " + trip.destination);
        System.out.println("Start Date   : " + trip.startDate);
        System.out.println("End Date     : " + trip.endDate);
        System.out.println("Total Budget : " + money(trip.budget));
        System.out.println("Members      : " + trip.members);
        System.out.println("Total People : " + trip.members.size());
    }

    // 2. Add activity. On a clash, the user is prompted to re-enter its details.
    static void addActivity() {
        heading("ADD NEW ACTIVITY");
        if (!tripExists()) return;

        while (true) {
            String name = readText("Enter Activity Name: ");
            LocalDate date = readDate("Enter Activity Date (YYYY-MM-DD): ");
            if (date.isBefore(trip.startDate) || date.isAfter(trip.endDate)) {
                error("Activity date is outside the trip duration. Please enter again.");
                continue;
            }

            LocalTime start = readTime("Enter Start Time (HH:MM AM/PM): ");
            LocalTime end = readTime("Enter End Time (HH:MM AM/PM): ");
            if (!end.isAfter(start)) {
                error("End time must be later than start time. Please enter the activity again.");
                continue;
            }

            Activity conflict = null;
            for (Activity a : trip.activities) {
                if (a.date.equals(date) && start.isBefore(a.endTime)
                        && end.isAfter(a.startTime)) {
                    conflict = a;
                    break;
                }
            }

            if (conflict != null) {
                heading("TIME CLASH DETECTED");
                System.out.println("New Activity      : " + name);
                System.out.println("Existing Activity : " + conflict.name);
                System.out.println("Existing Time     : " +
        formatTime(conflict.startTime) + " - " +
        formatTime(conflict.endTime));
                error("These activities overlap on " + date + ". Please enter the activity details again.");
                continue;
            }

            double cost = readDouble("Enter Planned Cost (Rs.): ");
            String payer = chooseMember("Who will pay for this activity? Enter member number: ");
            Activity activity = new Activity(name, date, start, end, cost, payer);
            trip.activities.add(activity);

            success("Activity added successfully!");
            System.out.println("Activity : " + name);
            System.out.println("Date     : " + date);
            System.out.println("Time     : " + formatTime(start) + " - " + formatTime(end));
            System.out.println("Cost     : " + money(cost));
            System.out.println("Paid By  : " + payer);
            return;
        }
    }

    // 3. Record actual spend for an activity. It is attributed to the payer selected
    // while adding the activity, so the contribution summary stays consistent.
    static void updateSpending() {
        heading("UPDATE ACTUAL SPENDING");
        if (!tripExists()) return;
        if (trip.activities.isEmpty()) {
            error("No activities available. Add activities first.");
            return;
        }

        printActivityChoices();
        int choice = readInt("Select Activity Number: ");
        if (choice < 1 || choice > trip.activities.size()) {
            error("Invalid activity number.");
            return;
        }

        Activity selected = trip.activities.get(choice - 1);
        System.out.println("Selected Activity : " + selected.name);
        System.out.println("Recorded Payer    : " + selected.paidBy);
        double amount = readDouble("Enter additional amount actually spent (Rs.): ");
        selected.actualCost += amount;

        success("Actual spending updated!");
        System.out.println("Activity        : " + selected.name);
        System.out.println("Actual Cost     : " + money(selected.actualCost));
        System.out.println("Contribution of " + selected.paidBy + " : " + money(getPaidBy(selected.paidBy)));
        System.out.println("Total Spending  : " + money(getActualCost()));
        System.out.println("Remaining Budget: " + money(trip.budget - getActualCost()));
        if (getActualCost() > trip.budget)
            error("Actual spending has exceeded the trip budget.");
    }

    static void printActivityChoices() {
        System.out.println("\nACTIVITIES:");
        for (int i = 0; i < trip.activities.size(); i++) {
            Activity a = trip.activities.get(i);
            System.out.println((i + 1) + ". " + a.name + " | " + a.date
                    + " | Planned: " + money(a.plannedCost)
                    + " | Actual: " + money(a.actualCost));
        }
        line();
    }

    // 4. Day-wise itinerary in a table.
    static void printItineraryTable(PrintStream out) {
        out.printf("%-12s | %-13s | %-25s | %-14s | %-14s | %-14s%n",
                "DATE", "TIME", "ACTIVITY", "PLANNED COST", "ACTUAL COST", "PAID BY");
        out.println("--------------+---------------+---------------------------+----------------+----------------+---------------");

        ArrayList<Activity> sorted = new ArrayList<>(trip.activities);
        sorted.sort(Comparator.comparing((Activity a) -> a.date)
                .thenComparing(a -> a.startTime));

        LocalDate day = trip.startDate;
        while (!day.isAfter(trip.endDate)) {
            boolean found = false;
            for (Activity a : sorted) {
                if (a.date.equals(day)) {
                    found = true;
                    out.printf("%-12s | %-13s | %-25s | %-14s | %-14s | %-14s%n",
                            day, formatTime(a.startTime) + "-" + formatTime(a.endTime),
shorten(a.name, 25),
                            money(a.plannedCost), money(a.actualCost), shorten(a.paidBy, 14));
                }
            }
            if (!found)
                out.printf("%-12s | %-13s | %-25s | %-14s | %-14s | %-14s%n",
                        day, "-", "No activities planned", "-", "-", "-");
            day = day.plusDays(1);
        }
    }


    static void printItineraryTable(PrintWriter out) {
        out.printf("%-12s | %-13s | %-25s | %-14s | %-14s | %-14s%n",
                "DATE", "TIME", "ACTIVITY", "PLANNED COST", "ACTUAL COST", "PAID BY");
        out.println("--------------+---------------+---------------------------+----------------+----------------+---------------");
        ArrayList<Activity> sorted = new ArrayList<>(trip.activities);
        sorted.sort(Comparator.comparing((Activity a) -> a.date)
                .thenComparing(a -> a.startTime));
        LocalDate day = trip.startDate;
        while (!day.isAfter(trip.endDate)) {
            boolean found = false;
            for (Activity a : sorted) {
                if (a.date.equals(day)) {
                    found = true;
                    out.printf("%-12s | %-13s | %-25s | %-14s | %-14s | %-14s%n",
                            day, formatTime(a.startTime) + "-" + formatTime(a.endTime), shorten(a.name, 25),
                            money(a.plannedCost), money(a.actualCost), shorten(a.paidBy, 14));
                }
            }
            if (!found)
                out.printf("%-12s | %-13s | %-25s | %-14s | %-14s | %-14s%n",
                        day, "-", "No activities planned", "-", "-", "-");
            day = day.plusDays(1);
        }
    }

    static String shorten(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, max - 3) + "...";
    }

    static void showItinerary() {
        heading("DAY-WISE TRIP ITINERARY");
        if (!tripExists()) return;
        System.out.println("TRIP        : " + trip.tripName);
        System.out.println("DESTINATION : " + trip.destination);
        System.out.println("DATES       : " + trip.startDate + " to " + trip.endDate);
        System.out.println("MEMBERS     : " + trip.members);
        System.out.println();
        printItineraryTable(System.out);
        showCostSummary();
    }

    static void showCostSummary() {
        heading("TRIP COST SUMMARY");
        double planned = getPlannedCost(), actual = getActualCost();
        System.out.println("Total Budget          : " + money(trip.budget));
        System.out.println("Total Planned Cost    : " + money(planned));
        System.out.println("Total Actual Spending : " + money(actual));
        System.out.println("Planned Cost Per Person: " + money(planned / trip.members.size()));
        System.out.println("Actual Cost Per Person: " + money(actual / trip.members.size()));
        System.out.println("Remaining vs Actual   : " + money(trip.budget - actual));
        showMemberSplit();
    }

    // Displays paid contribution, equal share, and net balance for every member.
    static void showMemberSplit() {
        double share = getActualCost() / trip.members.size();
        heading("PER-PERSON CONTRIBUTION SPLIT");
        System.out.printf("%-18s | %-16s | %-16s | %-16s%n",
                "MEMBER", "PAID CONTRIBUTION", "EQUAL SHARE", "NET BALANCE");
        System.out.println("-------------------+------------------+------------------+------------------");
        for (String member : trip.members) {
            double paid = getPaidBy(member);
            double balance = paid - share;
            System.out.printf("%-18s | %-16s | %-16s | %-16s%n",
                    shorten(member, 18), money(paid), money(share), money(balance));
        }
        System.out.println("Net balance: positive = member paid extra; negative = member owes the group.");
    }

    // 5. Budget analysis, activity removal, and destination suggestions.
    static void budgetAnalysis() {
        heading("BUDGET ANALYSIS");
        if (!tripExists()) return;

        double planned = getPlannedCost(), actual = getActualCost();
        System.out.println("Destination      : " + trip.destination);
        System.out.println("Total Trip Budget: " + money(trip.budget));
        System.out.println("Planned Cost     : " + money(planned));
        System.out.println("Actual Spending  : " + money(actual));
        System.out.println("Remaining Budget : " + money(trip.budget - actual));

        if (planned <= trip.budget)
            success("Planned activities are within the budget.");
        else
            error("Planned activities exceed the budget by " + money(planned - trip.budget) + ".");

        if (actual > trip.budget)
            error("Actual spending is already over budget by " + money(actual - trip.budget)
                    + ". Removing an activity cannot reverse money already spent.");

        // Let the user choose and actually remove activities with no recorded spending.
        while (getPlannedCost() > trip.budget || getActualCost() > trip.budget) {
            heading("CUT AN ACTIVITY TO MANAGE THE BUDGET");
            System.out.println("Choose an activity to remove. Activities with actual spending cannot be deleted.");
            boolean removable = false;
            for (int i = 0; i < trip.activities.size(); i++) {
                Activity a = trip.activities.get(i);
                if (a.actualCost == 0) {
                    removable = true;
                    System.out.println((i + 1) + ". " + a.name + " | Planned saving: "
                            + money(a.plannedCost) + " | Date: " + a.date);
                }
            }
            if (!removable) {
                error("No activity without recorded spending is available to remove.");
                break;
            }

            int choice = readInt("Enter activity number to cut (0 to stop): ");
            if (choice == 0) break;
            if (choice < 1 || choice > trip.activities.size()
                    || trip.activities.get(choice - 1).actualCost > 0) {
                error("Invalid choice. Select one of the removable activities shown.");
                continue;
            }
            Activity removed = trip.activities.remove(choice - 1);
            success("Removed activity: " + removed.name
                    + ". Planned saving: " + money(removed.plannedCost));
            System.out.println("Updated planned cost: " + money(getPlannedCost()));
        }

        if (getPlannedCost() <= trip.budget && getActualCost() <= trip.budget)
            success("Your updated itinerary and actual spending are within budget.");
        else if (getPlannedCost() <= trip.budget)
            error("Planned activities are within budget, but actual spending remains over budget. Removing activities cannot undo money already spent.");
        else
            error("The planned itinerary is still over budget. Run budget analysis again to remove more activities.");

        showMemberSplit();

        // Recommend activities only when the actual budget has room.
        double remaining = trip.budget - Math.max(getPlannedCost(), getActualCost());
        if (remaining > 0) {
            heading("OPTIONAL ACTIVITIES FOR " + trip.destination.toUpperCase());
            System.out.println("Available budget after considering planned and actual costs: " + money(remaining));
            System.out.println("Here are some additional activities and their estimated prices:");
            LinkedHashMap<String, Double> options = SUGGESTIONS.get(trip.destination);
            int index = 1;
            for (Map.Entry<String, Double> option : options.entrySet()) {
                String status = option.getValue() <= remaining ? "Within remaining budget" : "Exceeds remaining budget";
                System.out.println(index++ + ". " + option.getKey() + " - " + money(option.getValue())
                        + " (" + status + ")");
            }
        } else {
            System.out.println("\nNo remaining budget for additional suggested activities.");
        }
        line();
    }

    // 6. Save the current itinerary and summary as a text file.
    static void saveItinerary() {
        heading("SAVE YOUR ITINERARY");
        if (!tripExists()) return;

        // Create a separate folder for saved itineraries
File folder = new File("TripCraft_Itineraries");

if (!folder.exists()) {
    folder.mkdirs();
}

// Generate a unique filename for every save
String fileName = "TripCraft_" +
        trip.tripName.replaceAll("[^a-zA-Z0-9-_]", "_") +
        "_" + System.currentTimeMillis() + ".txt";

File outputFile = new File(folder, fileName);

try (PrintWriter writer =
        new PrintWriter(new FileWriter(outputFile))) {
            writer.println("TRIPCRAFT - ITINERARY & BUDGET PLANNER");
            writer.println("Trip Name: " + trip.tripName);
            writer.println("Destination: " + trip.destination);
            writer.println("Dates: " + trip.startDate + " to " + trip.endDate);
            writer.println("Budget: " + money(trip.budget));
            writer.println("Members: " + trip.members);
            writer.println();
            printItineraryTable(writer);
            writer.println("\nTotal planned cost: " + money(getPlannedCost()));
            writer.println("Total actual spending: " + money(getActualCost()));
            writer.println("Remaining budget: " + money(trip.budget - getActualCost()));
            writer.println("\nPER-PERSON CONTRIBUTION SPLIT");
            double share = getActualCost() / trip.members.size();
            for (String member : trip.members) {
                double paid = getPaidBy(member);
                writer.println(member + " | Paid: " + money(paid)
                        + " | Equal share: " + money(share)
                        + " | Net balance: " + money(paid - share));
            }
            success("Itinerary saved to " + outputFile.getName() + " in the TripCraft_Itineraries folder.");
        } catch (IOException e) {
            error("Unable to save the itinerary: " + e.getMessage());
        }
    }

    static void displayMenu() {
        System.out.println();
        line();
        System.out.println("                              TRIPCRAFT");
        System.out.println("                    ITINERARY & BUDGET PLANNER");
        line();
        System.out.println("  1. Create New Trip");
        System.out.println("  2. Add Activity");
        System.out.println("  3. Update Actual Spending");
        System.out.println("  4. View Day-wise Itinerary");
        System.out.println("  5. Budget Analysis & Suggestions");
        System.out.println("  6. Save Itinerary to File");
        System.out.println("  7. Exit");
        line();
    }

    public static void main(String[] args) {
        int choice;
        heading("WELCOME TO TRIPCRAFT");
        System.out.println("PLAN SMART. TRAVEL BETTER.");
        do {
            displayMenu();
            choice = readInt("Enter Your Choice (1-7): ");
            switch (choice) {
                case 1: createTrip(); break;
                case 2: addActivity(); break;
                case 3: updateSpending(); break;
                case 4: showItinerary(); break;
                case 5: budgetAnalysis(); break;
                case 6: saveItinerary(); break;
                case 7:
                    heading("THANK YOU!");
                    System.out.println("Thank you for using TripCraft. Have a safe and happy journey!");
                    break;
                default: error("Invalid choice! Please select between 1 and 7.");
            }
        } while (choice != 7);
        sc.close();
    }
}
