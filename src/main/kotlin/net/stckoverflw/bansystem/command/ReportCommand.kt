package net.stckoverflw.bansystem.command

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.schlaubi.mikbot.plugin.api.settings.guildAdminOnly
import dev.schlaubi.mikbot.plugin.api.util.kord
import dev.schlaubi.mikbot.plugin.api.util.safeGuild
import kotlinx.datetime.Clock
import net.stckoverflw.bansystem.BansystemCommandModule
import net.stckoverflw.bansystem.database.Database
import net.stckoverflw.bansystem.database.model.BannedUser
import net.stckoverflw.bansystem.util.banButton
import net.stckoverflw.bansystem.util.scanAllGuilds

suspend fun BansystemCommandModule.reportCommand() = ephemeralSlashCommand(::ReportCommandArguments) {
    name = "report"
    description = "Report a User"

    guildAdminOnly()

    action {
        if (arguments.user.isBot) {
            respond {
                content = "The User can't be a Bot"
            }
            return@action
        }

        val doc = Database.bannedUserCollection.findOneById(arguments.user.id)
        if (doc != null) {
            Database.bannedUserCollection.save(
                doc.copy(
                    reasons = doc.reasons + arguments.reason
                )
            )
            respond {
                content = "This User is already reported" +
                        (if (doc.reasons.isNotEmpty()) "for ${doc.reasons.joinToString(",", "`", "`")}." else ".") +
                        ("Your Reason was added, though.")
                if (safeGuild.withStrategy(EntitySupplyStrategy.rest).getMemberOrNull(arguments.user.id) != null) {
                    banButton(arguments.user)
                }
            }
            return@action
        }

        Database.bannedUserCollection.save(BannedUser(
            arguments.user.id,
            this.safeGuild.id,
            this.user.id,
            Clock.System.now(),
            arrayListOf(arguments.reason)
        ))

        respond {
            content = "Your Report was sent and recorded"
            if (safeGuild.getMemberOrNull(arguments.user.id) != null) {
                banButton(arguments.user)
            }
        }

        scanAllGuilds(kord, arguments.user)

    }
}

class ReportCommandArguments : Arguments() {
    val user by user {
        name = "user"
        description = "user or id of user to report"
    }
    val reason by string {
        name = "reason"
        description = "reason what this user did"
    }
}
