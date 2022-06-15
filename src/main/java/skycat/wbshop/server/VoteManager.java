package skycat.wbshop.server;

import skycat.wbshop.WBShopServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class VoteManager {
    private static final File SAVE_FILE = new File("WBShopVoteManagerSave.txt");
    private final ArrayList<VotePolicy> VOTE_POLICIES;

    private VoteManager() {
        VOTE_POLICIES = new ArrayList<>(); // TODO
        // WARN: This is for debugging, it should be deleted for use.
        VOTE_POLICIES.add(new VotePolicy(10000, new ArrayList<>()));
    }

    public boolean saveToFile() throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(SAVE_FILE);
        printWriter.write(WBShopServer.GSON.toJson(this));
        printWriter.close();
        return true;
    }

    public static VoteManager loadOrMake() {
        try {
            return loadFromFile();
        } catch (FileNotFoundException e) {
            System.out.println("Error loading VoteManager from file. Creating a new one");
        }
        return new VoteManager();
    }

    private static VoteManager loadFromFile() throws FileNotFoundException {
        String jsonString = "";
        Scanner fileScanner = new Scanner(SAVE_FILE);
        while (fileScanner.hasNextLine()) {
            jsonString += fileScanner.nextLine();
        }
        return WBShopServer.GSON.fromJson(jsonString, VoteManager.class);
    }


    public void addVote(Vote vote, int policyNumber) {
        VOTE_POLICIES.get(policyNumber).addVote(vote); // TODO: Make this handle things if the policy doesn't exist
    }

    /**
     * Add a vote to a given policy
     * @param vote The vote for the policy
     * @param votePolicy The policy to vote for
     * @return true if the policy has been registered, false if the policy was not registered
     */
    public boolean addVote(Vote vote, VotePolicy votePolicy) {
        if (VOTE_POLICIES.contains(votePolicy)) { // TODO: Make this handle things if the policy doesn't exist
            votePolicy.addVote(vote);
            return true;
        }
        return false;
    }

    public ArrayList<VotePolicy> getVotePolicies() {
        return VOTE_POLICIES;
    }

    // count votes, remove votes, other util methods

}
