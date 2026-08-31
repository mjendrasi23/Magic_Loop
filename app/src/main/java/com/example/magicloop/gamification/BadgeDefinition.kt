package com.example.magicloop.gamification

enum class BadgeId {
    FIRST_PROJECT_STARTED,
    FIRST_PROJECT_COMPLETED,
    FIVE_PROJECTS_COMPLETED,
    STREAK_3_DAYS,
    STREAK_7_DAYS,
    STREAK_30_DAYS,
    FIRST_PATTERN_IMPORTED,
    FIRST_COUNTER_TARGET_REACHED,
    FIRST_PHOTO_ADDED,
    STASH_FIRST_YARN_ADDED
}

data class BadgeDefinition(
    val id: BadgeId,
    val title: String,
    val description: String,
    val iconName: String
)

object BadgeCatalog {
    val all: List<BadgeDefinition> = listOf(
        BadgeDefinition(
            BadgeId.FIRST_PROJECT_STARTED,
            "Prvi koraci",
            "Započeo/la si svoj prvi projekt",
            "PlayArrow"
        ),
        BadgeDefinition(
            BadgeId.FIRST_PROJECT_COMPLETED,
            "Završeno!",
            "Dovršio/la si svoj prvi projekt",
            "CheckCircle"
        ),
        BadgeDefinition(
            BadgeId.FIVE_PROJECTS_COMPLETED,
            "Iskusna ruka",
            "Dovršeno je 5 projekata",
            "Stars"
        ),
        BadgeDefinition(
            BadgeId.STREAK_3_DAYS,
            "Zagrijavanje",
            "3 dana zaredom u nizu",
            "LocalFireDepartment"
        ),
        BadgeDefinition(
            BadgeId.STREAK_7_DAYS,
            "Tjedan dana",
            "7 dana zaredom u nizu",
            "LocalFireDepartment"
        ),
        BadgeDefinition(
            BadgeId.STREAK_30_DAYS,
            "Nezaustavljivo",
            "30 dana zaredom u nizu",
            "LocalFireDepartment"
        ),
        BadgeDefinition(
            BadgeId.FIRST_PATTERN_IMPORTED,
            "Čitač shema",
            "Uvezao/la si svoju prvu shemu",
            "Description"
        ),
        BadgeDefinition(
            BadgeId.FIRST_COUNTER_TARGET_REACHED,
            "Cilj ostvaren",
            "Dosegnuo/la si postavljeni cilj brojača",
            "Flag"
        ),
        BadgeDefinition(
            BadgeId.FIRST_PHOTO_ADDED,
            "Trenutak za sjećanje",
            "Dodao/la si prvu fotografiju projekta",
            "PhotoCamera"
        ),
        BadgeDefinition(
            BadgeId.STASH_FIRST_YARN_ADDED,
            "Organizator zaliha",
            "Dodao/la si prvu vunu u zalihu",
            "Inventory"
        )
    )

    fun get(id: BadgeId): BadgeDefinition = all.first { it.id == id }
}