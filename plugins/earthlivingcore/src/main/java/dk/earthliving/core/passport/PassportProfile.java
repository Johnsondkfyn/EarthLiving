package dk.earthliving.core.passport;

import java.util.Map;

public record PassportProfile(
        String uuid,
        String playerName,
        String issuedAt,
        String citizenshipCountry,
        String citizenshipStatus,
        String citizenshipGrantedAt,
        Map<String, VisaEntry> visas,
        Map<String, Integer> reputation
) {
    public record VisaEntry(
            String country,
            String type,
            String status,
            String issuedAt,
            String expiresAt
    ) {
    }
}
