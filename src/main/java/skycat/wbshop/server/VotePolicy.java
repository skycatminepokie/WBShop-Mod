package skycat.wbshop.server;

import java.util.ArrayList;
import java.util.List;

public class VotePolicy {
    private final int voteTotal;
    private final ArrayList<Vote> votes;
    private int votesRequired;

    public VotePolicy(int votesRequired, ArrayList<Vote> votes) {
        this.votesRequired = votesRequired;
        this.votes = votes;
        voteTotal = 0;
    }

    public void addVote(Vote vote) {
        votes.add(vote); // TODO: Error detection?
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

    public ArrayList<Vote> getVotes() {
        return votes;
    }

    public int getVotesRequired() {
        return votesRequired;
    }

    public void setVotesRequired(int votesRequired) {
        this.votesRequired = votesRequired;
    }

    private void updateVoteTotal() {
        //TODO
    }

}
