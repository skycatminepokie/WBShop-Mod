package skycat.wbshop.server;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a vote made in favor or against a {@link VotePolicy}
 */
public class Vote {
    private final UUID voterUUID;
    private final int amount;
    private final LocalDateTime voteTime;

    public Vote(UUID voterUUID, int amount, LocalDateTime voteTime) {
        this.voterUUID = voterUUID;
        this.amount = amount;
        this.voteTime = voteTime;
    }

    public int getAmount() {
        return amount;
    }

    public LocalDateTime getVoteTime() {
        return voteTime;
    }

    public UUID getVoterUUID() {
        return voterUUID;
    }
}
