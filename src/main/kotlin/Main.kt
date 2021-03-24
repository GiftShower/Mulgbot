package com.example
import com.jessecorbett.diskord.api.DiscordUserType
import com.jessecorbett.diskord.api.model.*
import com.jessecorbett.diskord.api.rest.MessageEdit
import com.jessecorbett.diskord.api.rest.client.*
import com.jessecorbett.diskord.dsl.*
import com.jessecorbett.diskord.util.*

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
                val ejS = mutableListOf("-1")
                val want = words.drop(1).joinToString(" ")
                for(i in 0 until (emjlist.size - 1)){
                    if (doublecut(emjlist[i].toString(), "name=", ",") == want){
                        ejS[0] = i.toString()
                        break
                    }
                }
                val wVal = ejS[0].toInt()
                if(wVal != -1){
                    ejS.add(doublecut(emjlist[wVal].toString(), "isAnimated=", ")"))
                    ejS.add(doublecut(emjlist[wVal].toString(), "name=", ","))
                    ejS.add(doublecut(emjlist[wVal].toString(), "id=", ","))
                    if(ejS[1].toBoolean()){
                        reply("${doublecut(this.author.toString(),
                            "username=",
                            ", discriminator")}:  <a:${ejS[2]}:${ejS[3]}>")
                    }
                    else {
                        reply("GIF가 아닙니다!")
                    }
                }
                else{
                    reply("없는 이모티콘입니다!")
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
                    description = "-=help: 명령어 목록을 표시합니다.\n-=req: 건의채널에서 건의를 보냅니다.\n-=domain: 서버 주소를 보여줍니다.\n" +
                            "-=gifemoji <이모티콘 이름(: 없이)>: gif 이모티콘을 하나 전송합니다."
                }
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
            if (it.channelId == "759308637385654323") {
                if (it.authorId != "777135853608370217") {
                    it.delete()
                }
            }
            //screenshot channel message without embed delete
            if (it.channelId == "759364798516953099") {
                if (it.attachments.isEmpty()) {
                    it.delete()
                }
            }
        }

    }
}
suspend fun isRole(guildClient: GuildClient, user: GuildMember, roleName: String) = guildClient.getRoles()
    .any { role -> user.roleIds.contains(role.id) && role.name == roleName }

fun doublecut(toCut: String, dlA: String, dlB: String) = toCut.substringAfter(dlA).substringBefore(dlB)
