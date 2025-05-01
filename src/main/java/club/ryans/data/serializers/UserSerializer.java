package club.ryans.data.serializers;

import club.ryans.data.entities.BuildingLevelRow;
import club.ryans.data.entities.DailyConfigurationRow;
import club.ryans.data.entities.DailyIdRow;
import club.ryans.data.entities.InviteRow;
import club.ryans.data.entities.OfficerLevelRow;
import club.ryans.data.entities.ResearchLevelRow;
import club.ryans.data.entities.ResourceAmountRow;
import club.ryans.data.entities.ShipRow;
import club.ryans.data.entities.UserAccountRow;
import club.ryans.data.entities.UserRow;
import club.ryans.data.repositories.BuildingLevelRepository;
import club.ryans.data.repositories.DailyConfigurationRepository;
import club.ryans.data.repositories.DailyIdRepository;
import club.ryans.data.repositories.InviteRepository;
import club.ryans.data.repositories.OfficerLevelRepository;
import club.ryans.data.repositories.ResearchLevelRepository;
import club.ryans.data.repositories.ResourceAmountRepository;
import club.ryans.data.repositories.ShipRepository;
import club.ryans.data.repositories.UserAccountRepository;
import club.ryans.data.repositories.UserRepository;
import club.ryans.models.items.Building;
import club.ryans.models.items.Research;
import club.ryans.models.items.Resource;
import club.ryans.models.managers.ItemManagerContainer;
import club.ryans.models.player.BuildingLevel;
import club.ryans.models.player.BuildingLevelDelta;
import club.ryans.models.player.DailyConfiguration;
import club.ryans.models.player.DailyConfigurations;
import club.ryans.models.player.Invite;
import club.ryans.models.player.OfficerLevel;
import club.ryans.models.player.OfficerLevelDelta;
import club.ryans.models.player.PlayerItems;
import club.ryans.models.player.PlayerItemsDelta;
import club.ryans.models.player.ResearchLevel;
import club.ryans.models.player.ResearchLevelDelta;
import club.ryans.models.accounting.ResourceAmount;
import club.ryans.models.player.ResourceAmountDelta;
import club.ryans.models.player.Ship;
import club.ryans.models.player.ShipDelta;
import club.ryans.models.player.User;
import club.ryans.models.player.UserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSerializer {
    private final ItemManagerContainer itemManagerContainer;

    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final InviteRepository inviteRepository;
    private final ResourceAmountRepository resourceAmountRepository;
    private final BuildingLevelRepository buildingLevelRepository;
    private final ShipRepository shipRepository;
    private final OfficerLevelRepository officerLevelRepository;
    private final ResearchLevelRepository researchLevelRepository;

    private final DailyConfigurationRepository dailyConfigurationRepository;
    private final DailyIdRepository dailyIdRepository;

    private Map<String, Integer> dailyIdMap;
    private ArrayList<String> dailyIdList;

    public User lookupUserById(final int id) {
        return createUser(userRepository.findById(id));
    }

    public User lookupUserByDiscordId(final long discordId) {
        return createUser(userRepository.findByDiscordId(discordId));
    }

    @Transactional
    public List<User> lookupAllUsers() {
        Iterable<UserRow> userRows = userRepository.findAll();
        Iterable<UserAccountRow> accountRows = userAccountRepository.findAll();

        Map<Integer, UserAccountRow> accountMap = new HashMap<>();
        for (UserAccountRow accountRow : accountRows) {
            accountMap.put(accountRow.getId(), accountRow);
        }

        List<User> users = new ArrayList<>();
        for (UserRow userRow : userRows) {
            users.add(createUser(userRow, accountMap.get(userRow.getAccountId())));
        }
        return users;
    }

    public boolean hasSyncToken(final String syncToken) {
        return userAccountRepository.existsBySyncToken(syncToken);
    }

    public boolean hasInviteToken(final String inviteToken) {
        return inviteRepository.existsByToken(inviteToken);
    }

    public User save(final User user) {
        UserAccount account = user.getAccount();
        if (account != null)  {
            save(account);
        }

        UserRow userRow = createUserRow(user);
        userRepository.save(userRow);
        user.setId(userRow.getId());
        return user;
    }

    @Transactional
    public User save(final User user, final Invite invite) {
        save(invite);
        return save(user);
    }

    public UserAccount save(final UserAccount account) {
        if (account != null)  {
            UserAccountRow userAccountRow = createUserAccountRow(account);
            userAccountRepository.save(userAccountRow);
            account.setId(userAccountRow.getId());
        }

        return account;
    }

    public Invite save(final Invite invite) {
        InviteRow inviteRow = createInviteRow(invite);
        inviteRepository.save(inviteRow);
        invite.setId(inviteRow.getId());
        return invite;
    }

    public Integer lookupUserIdBySyncToken(final String syncToken) {
        UserAccountRow userAccountRow = userAccountRepository.findBySyncToken(syncToken);

        if (userAccountRow == null) {
            return 0;
        }

        return userRepository.findUserIdByAccountId(userAccountRow.getId());
    }

    public PlayerItems loadAccountItems(final UserAccount account) {
        final int accountId = account.getId();
        PlayerItems items = new PlayerItems();

        List<ResourceAmountRow> resourceAmountRows = resourceAmountRepository.findAllByAccountId(accountId);
        resourceAmountRows.forEach(resourceAmountRow -> items.addResource(createResource(resourceAmountRow)));

        List<BuildingLevelRow> buildingLevelRows = buildingLevelRepository.findAllByAccountId(accountId);
        buildingLevelRows.forEach(buildingLevelRow -> items.addBuilding(createBuilding(buildingLevelRow)));

        List<ShipRow> shipRows = shipRepository.findAllByAccountId(accountId);
        shipRows.forEach(shipRow -> items.addShip(createShip(shipRow)));

        List<OfficerLevelRow> officerLevelRows = officerLevelRepository.findAllByAccountId(accountId);
        officerLevelRows.forEach(officerLevelRow -> items.addOfficer(createOfficer(officerLevelRow)));

        List<ResearchLevelRow> researchLevelRows = researchLevelRepository.findAllByAccountId(accountId);
        researchLevelRows.forEach(researchLevelRow -> items.addResearchLevel(createResearchLevel(researchLevelRow)));

        return items;
    }

    public DailyConfigurations loadDailyConfigurations(final int accountId) {
        List<DailyConfiguration> dailies = new ArrayList<>();
        for (DailyConfigurationRow dailyConfigurationRow : dailyConfigurationRepository.findAllByAccountId(accountId)) {
            dailies.add(createDailyConfiguration(dailyConfigurationRow));
        }
        return new DailyConfigurations(dailies);
    }

    public DailyConfiguration loadDailyConfiguration(final int accountId, final String dailyIdString) {
        int dailyId = lookupDailyId(dailyIdString);
        DailyConfigurationRow dailyConfigurationRow =
                dailyConfigurationRepository.findByAccountIdAndDailyId(accountId, dailyId);
        return createDailyConfiguration(dailyConfigurationRow);
    }

    public void save(final UserAccount account, final DailyConfiguration dailyConfiguration) {
        DailyConfigurationRow dailyConfigurationRow = createDailyConfigurationRow(account.getId(), dailyConfiguration);
        dailyConfigurationRepository.save(dailyConfigurationRow);
    }

    public Invite getInviteByToken(final String inviteToken) {
        InviteRow inviteRow = inviteRepository.findByToken(inviteToken);
        return createInvite(inviteRow);
    }

    @Transactional
    public Collection<Invite> getInvites() {
        List<Invite> invites = new ArrayList<>();
        for (InviteRow inviteRow : inviteRepository.findAll()) {
            invites.add(createInvite(inviteRow));
        }
        return invites;
    }

    @Transactional
    public void save(final UserAccount account, final PlayerItemsDelta delta) {
        if (delta.getResources().size() > 0) {
            List<ResourceAmountRow> resourceRows = delta.getResources().stream().map(resourceAmountDelta
                    -> createResourceRow(account.getId(), resourceAmountDelta)).collect(Collectors.toList());
            resourceAmountRepository.saveAll(resourceRows);
        }

        if (delta.getBuildings().size() > 0) {
            List<BuildingLevelRow> buildingRows = delta.getBuildings().stream().map(buildingLevelDelta
                    -> createBuildingRow(account.getId(), buildingLevelDelta)).collect(Collectors.toList());
            buildingLevelRepository.saveAll(buildingRows);
        }

        if (delta.getships().size() > 0) {
            List<ShipRow> shipRows = delta.getships().stream().map(shipDelta
                    -> createShipRow(account.getId(), shipDelta)).collect(Collectors.toList());
            shipRepository.saveAll(shipRows);
        }

        if (delta.getOfficers().size() > 0) {
            List<OfficerLevelRow> officerRows = delta.getOfficers().stream().map(officerLevelDelta
                    -> createOfficerRow(account.getId(), officerLevelDelta)).collect(Collectors.toList());
            officerLevelRepository.saveAll(officerRows);
        }

        if (delta.getResearchLevels().size() > 0) {
            List<ResearchLevelRow> researchLevelRows = delta.getResearchLevels().stream().map(researchLevelDelta
                    -> createResearchLevelRow(account.getId(), researchLevelDelta)).collect(Collectors.toList());
            researchLevelRepository.saveAll(researchLevelRows);
        }
    }

    private int lookupDailyId(final String dailyIdString) {
        initializeDailyIdMap();
        if (!dailyIdMap.containsKey(dailyIdString)) {
            DailyIdRow dailyIdRow = new DailyIdRow();
            dailyIdRow.setIdString(dailyIdString);
            dailyIdRepository.save(dailyIdRow);
            insertDailyId(dailyIdRow.getId(), dailyIdRow.getIdString());
        }
        return dailyIdMap.get(dailyIdString);
    }

    private String lookupDailyId(final int dailyId) {
        initializeDailyIdMap();
        return dailyIdList.get(dailyId);
    }

    private void initializeDailyIdMap() {
        if (dailyIdMap != null) {
            return;
        }

        dailyIdMap = new HashMap<>();
        dailyIdList = new ArrayList<>();
        for (DailyIdRow dailyIdRow : dailyIdRepository.findAll()) {
            insertDailyId(dailyIdRow.getId(), dailyIdRow.getIdString());
        }
    }

    private void insertDailyId(final int dailyId, final String dailyIdString) {
        dailyIdMap.put(dailyIdString, dailyId);
        if (dailyId >= dailyIdList.size()) {
            dailyIdList.ensureCapacity(dailyId + 1);
        }
        while (dailyId >= dailyIdList.size()) {
            dailyIdList.add(null);
        }
        dailyIdList.set(dailyId, dailyIdString);
    }

    private User createUser(final UserRow userRow) {
        if (userRow == null) {
            return null;
        }

        UserAccountRow userAccountRow = userRow.getAccountId() == null ? null :
            userAccountRepository.findById((int) userRow.getAccountId());

        return createUser(userRow, userAccountRow);
    }

    private User createUser(final UserRow userRow, final UserAccountRow userAccountRow) {
        Instant registrationTime = Utility.getTime(userRow.getRegistrationTime());
        UserAccount userAccount = createUserAccount(userAccountRow);
        return new User(userRow.getId(), userRow.getFlags(), userRow.getDiscordName(), userRow.getDiscordId(),
                registrationTime, userAccount);
    }

    private UserRow createUserRow(final User user) {
        UserRow userRow = new UserRow();
        userRow.setId(user.getId());
        userRow.setFlags(user.getFlags());
        userRow.setDiscordName(user.getDiscordName());
        userRow.setDiscordId(user.getDiscordId());
        userRow.setRegistrationTime(Utility.getPersistTime(user.getRegistrationTime()));
        userRow.setAccountId(user.getAccount() != null ? user.getAccount().getId() : 0);
        return userRow;
    }

    private UserAccountRow createUserAccountRow(final UserAccount account) {
        UserAccountRow userAccountRow = new UserAccountRow();
        userAccountRow.setId(account.getId());
        userAccountRow.setFlags(account.getFlags());
        userAccountRow.setServer(account.getServer());
        userAccountRow.setGameName(account.getGameName());
        userAccountRow.setSyncToken(account.getSyncToken());
        userAccountRow.setSpocksClubSyncToken(account.getSpocksClubSyncToken());
        return userAccountRow;
    }

    private UserAccount createUserAccount(final UserAccountRow userAccountRow) {
        if (userAccountRow == null) {
            return null;
        }
        return new UserAccount(userAccountRow.getId(), userAccountRow.getFlags(), userAccountRow.getServer(),
            userAccountRow.getGameName(), userAccountRow.getSyncToken(), userAccountRow.getSpocksClubSyncToken());
    }

    private ResourceAmountRow createResourceRow(final int accountId, final ResourceAmountDelta resourceAmountDelta) {
        ResourceAmountRow resourceRow = new ResourceAmountRow();
        resourceRow.setAccountId(accountId);
        resourceRow.setResourceId(resourceAmountDelta.getResourceId());
        resourceRow.setAmount(resourceAmountDelta.getCurrent().getAmount());
        return resourceRow;
    }

    private ResourceAmount createResource(final ResourceAmountRow resourceAmountRow) {
        final Resource resource = itemManagerContainer.getResource(resourceAmountRow.getResourceId());
        return new ResourceAmount(resource, resourceAmountRow.getAmount());
    }

    private BuildingLevelRow createBuildingRow(final int accountId, final BuildingLevelDelta buildingLevelDelta) {
        BuildingLevelRow buildingRow = new BuildingLevelRow();
        buildingRow.setAccountId(accountId);
        buildingRow.setBuildingId(buildingLevelDelta.getBuildingId());
        buildingRow.setLevel(buildingLevelDelta.getCurrent().getLevel());
        return buildingRow;
    }

    private BuildingLevel createBuilding(final BuildingLevelRow buildingLevelRow) {
        Building building = itemManagerContainer.getBuilding(buildingLevelRow.getBuildingId());
        BuildingLevel buildingLevel = new BuildingLevel(building, buildingLevelRow.getLevel());
        return buildingLevel;
    }

    private ShipRow createShipRow(final int accountId, final ShipDelta shipDelta) {
        ShipRow shipRow = new ShipRow();
        shipRow.setAccountId(accountId);
        shipRow.setShipId(shipDelta.getShipId());
        shipRow.setShipClassId(shipDelta.getCurrent().getShipClassId());
        shipRow.setTier(shipDelta.getCurrent().getTier());
        shipRow.setLevel(shipDelta.getCurrent().getLevel());
        return shipRow;
    }

    private Ship createShip(final ShipRow shipRow) {
        Ship ship = new Ship();
        ship.setId(shipRow.getShipId());
        ship.setShipClass(itemManagerContainer.getShipClass(shipRow.getShipClassId()));
        ship.setTier(shipRow.getTier());
        ship.setLevel(shipRow.getLevel());
        return ship;
    }

    private OfficerLevelRow createOfficerRow(final int accountId, final OfficerLevelDelta officerLevelDelta) {
        OfficerLevelRow officerRow = new OfficerLevelRow();
        officerRow.setAccountId(accountId);
        officerRow.setOfficerId(officerLevelDelta.getOfficerId());
        officerRow.setRank(officerLevelDelta.getCurrent().getRank());
        officerRow.setLevel(officerLevelDelta.getCurrent().getLevel());
        return officerRow;
    }

    private OfficerLevel createOfficer(final OfficerLevelRow officerLevelRow) {
        OfficerLevel officerLevel = new OfficerLevel();
        officerLevel.setOfficer(itemManagerContainer.getOfficer(officerLevelRow.getOfficerId()));
        officerLevel.setRank(officerLevelRow.getRank());
        officerLevel.setLevel(officerLevelRow.getLevel());
        return officerLevel;
    }

    private ResearchLevelRow createResearchLevelRow(final int accountId, final ResearchLevelDelta researchLevelDelta) {
        ResearchLevelRow researchLevelRow = new ResearchLevelRow();
        researchLevelRow.setAccountId(accountId);
        researchLevelRow.setResearchId(researchLevelDelta.getResearchId());
        researchLevelRow.setLevel(researchLevelDelta.getCurrent().getLevel());
        return researchLevelRow;
    }

    private ResearchLevel createResearchLevel(final ResearchLevelRow researchLevelRow) {
        final Research research = itemManagerContainer.getResearch(researchLevelRow.getResearchId());
        return new ResearchLevel(research, researchLevelRow.getLevel());
    }

    private InviteRow createInviteRow(final Invite invite) {
        InviteRow inviteRow = new InviteRow();
        inviteRow.setId(invite.getId());
        inviteRow.setState(invite.getState());
        inviteRow.setToken(invite.getToken());
        inviteRow.setCreatorId(invite.getCreator().getId());
        inviteRow.setCreationTime(Utility.getPersistTime(invite.getCreationTime()));
        inviteRow.setUserId(Utility.getUserId(invite.getUser()));
        inviteRow.setUseTime(Utility.getPersistTime(invite.getUseTime()));
        return inviteRow;
    }

    private Invite createInvite(final InviteRow inviteRow) {
        if (inviteRow == null) {
            return null;
        }

        User creator = lookupUserById(inviteRow.getCreatorId());
        Instant creationTime = Utility.getTime(inviteRow.getCreationTime());
        User user = inviteRow.getUserId() == null ? null : lookupUserById(inviteRow.getUserId());
        Instant useTime = Utility.getTime(inviteRow.getUseTime());
        return new Invite(inviteRow.getId(), inviteRow.getState(), inviteRow.getToken(), creator,
                creationTime, user, useTime);
    }

    private DailyConfiguration createDailyConfiguration(final DailyConfigurationRow dailyConfigurationRow) {
        if (dailyConfigurationRow == null) {
            return null;
        }
        final String dailyId = lookupDailyId(dailyConfigurationRow.getDailyId());
        return new DailyConfiguration(dailyId, dailyConfigurationRow.getFlags(), dailyConfigurationRow.getChests(),
            dailyConfigurationRow.getGoal());
    }

    private DailyConfigurationRow createDailyConfigurationRow(final int accountId,
            final DailyConfiguration dailyConfiguration) {
        final int dailyId = lookupDailyId(dailyConfiguration.getDailyId());
        DailyConfigurationRow dailyConfigurationRow = new DailyConfigurationRow();
        dailyConfigurationRow.setAccountId(accountId);
        dailyConfigurationRow.setDailyId(dailyId);
        dailyConfigurationRow.setFlags(dailyConfiguration.getFlags());
        dailyConfigurationRow.setChests(dailyConfiguration.getChests());
        dailyConfigurationRow.setGoal(dailyConfiguration.getGoal());
        return dailyConfigurationRow;
    }
}