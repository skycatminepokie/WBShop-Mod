package skycat.wbshop.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class VotePolicy {
    private int voteTotal;
    private int votesRequired;
    private ArrayList<Vote> votes;

    public VotePolicy(int votesRequired, ArrayList<Vote> votes) {
        this.votesRequired = votesRequired;
        this.votes = votes;
        voteTotal = 0;
    }

    public int getVotesRequired() {
        return votesRequired;
    }

    public void setVotesRequired(int votesRequired) {
        this.votesRequired = votesRequired;
    }

    public ArrayList<Vote> getVotes() {
        return votes;
    }

    public void addVote(Vote vote) {
        votes.add(vote);
    }

    public void addVotes(ArrayList<Vote> votes) {
        this.votes.addAll(votes);
    }

    public void addVotes(Vote... votes) {
        this.votes.addAll(List.of(votes));
    }

    public int getVoteTotal() {
        updateVoteTotal();
        return voteTotal;
    }

    private void updateVoteTotal() {
        //TODO
    }

}
