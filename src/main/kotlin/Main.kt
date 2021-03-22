package com.example
import com.jessecorbett.diskord.api.DiscordUserType
import com.jessecorbett.diskord.api.model.Emoji
import com.jessecorbett.diskord.api.model.GuildMember
import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.api.model.Role
import com.jessecorbett.diskord.api.rest.MessageEdit
import com.jessecorbett.diskord.api.rest.client.ChannelClient
import com.jessecorbett.diskord.api.rest.client.DiscordClient
import com.jessecorbett.diskord.api.rest.client.GuildClient
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.authorId
import com.jessecorbett.diskord.util.words
import com.jessecorbett.diskord.util.DiskordInternals
import com.jessecorbett.diskord.util.sendMessage

private val BOT_TOKEN = try {
    ClassLoader.getSystemResource("bot-token.txt").readText().trim()
} catch (error: Exception) {
    throw RuntimeException("Failed to load bot token. Make sure to create a file named bot-token.txt in" +
            " src/main/resources and paste the bot token into that file.", error)
}

@DiskordInternals
suspend fun main() {
    bot(BOT_TOKEN) {
        val gc = GuildClient(
            BOT_TOKEN,
            "661514065679482900"
        )
        commands("-=") {
            command("test") {
                reply {
                    title = "test"
                    description = "테스트 성공"
                }
            }
            command("gifemoji"){
                val emjlist: List<Emoji> = gc.getEmoji()
                val want = words.drop(1).joinToString(" ")
                var enamt: String
                var wantval: Int = emjlist.size
                for(i in 0 until (emjlist.size - 1)){
                    enamt = emjlist[i].toString().substringAfter("name=").substringBefore(",")
                    if (enamt == want){
                        i.also { wantval = it }
                        break
                    }
                }
                this.delete()
                val emjnam = emjlist[wantval].toString().substringAfter("name=").substringBefore(",")
                val emjid = emjlist[wantval].toString().substringAfter("id=").substringBefore(",")
                val emjanim = emjlist[wantval].toString().substringAfter("isAnimated=").substringBefore(")").toBoolean()
                if(emjanim && wantval != 28){
                    reply("<a:$emjnam:$emjid>")
                }
                else{
                    reply("Not Animated or emoji does not exist.")
                }

            }
            command("getuser"){
                val dc = DiscordClient(BOT_TOKEN)
                val usr = this.author
                val udat = dc.getUser(this.authorId)
                reply{
                    title = "User: $usr"
                    description = "$udat"
                }
            }
            command("isadmin"){
                if(isRole(guildClient = gc, gc.getMember(authorId), "관리자")) reply("관리자네요")
                else reply("관리자가 아니시군요")
            }
            command("roles"){
                val rllst = mutableListOf<String>()
                val glR: List<Role> = gc.getRoles()
                var grtmp : String
                var grfin : String
                for(i in 0 until (glR.size - 1)){
                    grtmp = glR[i].toString().substringAfter("name=")
                    grfin = grtmp.substringBefore(", color")
                    rllst.add("$grfin ")
                }
                reply{
                    title = "역할 목록"
                    description = (rllst.toString())
                }
            }
            command("domain") {
                reply {
                    title = "서버 주소"
                    description = "multiground.kro.kr:25560"
                }
            }
            command("help") {
                reply {
                    title = "멀그봇 명령어 목록"
                    description = "-=help: 명령어 목록을 표시합니다.\n-=req: 건의채널에서 건의를 보냅니다.\n-=domain: 서버 주소를 보여줍니다.\n"
                }
            }
            //서버 readme
            command("readmedotmd") {
                if (this.authorId.equals("364758752051855360")) {
                    reply {
                        title = "멀티그라운드에 오신 것을 환영합니다!"
                        description = "법전을 반드시 읽어 주시길 바랍니다.\n아래 역할을 눌러 노래봇 자유 컨트롤 권한을 획득하실 수 있습니다." +
                                "\n이 공지는 테스트용으로 제작되었습니다."
                    }
                }
            }

            command("closevote") {
                val mestok = words.drop(1).joinToString(" ")
                reply {
                    title = "투표가 종료되었습니다!"
                    description = "$mestok 토큰의 투표가 종료됨"
                }
                channel.deleteMessage(mestok)
            }

        }
        messageCreated {
            //need to add channel id
            if (it.content.contains("투표:")) {
                it.react("✅")
                it.react("❌")
            }
            //need to add channel id
            else if (it.content.contains("모집:")) {
                it.react("✋")
            }
            //request channel messege sent is not bot
            if (it.channelId.equals("759308637385654323")) {
                if (!it.authorId.equals("777135853608370217")) {
                    it.delete()
                }
            }
            //screenshot channel message without embed delete
            if (it.channelId.equals("759364798516953099")) {
                if (it.attachments.isEmpty()) {
                    it.delete()
                }
            }
        }

    }
}
suspend fun isRole(guildClient: GuildClient, user: GuildMember, roleName: String) = guildClient.getRoles()
    .any { role -> user.roleIds.contains(role.id) && role.name == roleName }
