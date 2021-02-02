package com.example

import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.authorId
import com.jessecorbett.diskord.util.words


/*
 * This can be replaced with any method to load the bot token.  This specific method is provided only for convenience
 * and as a way to prevent accidental disclosure of bot tokens.
 */
private val BOT_TOKEN = try {
    ClassLoader.getSystemResource("bot-token.txt").readText().trim()
} catch (error: Exception) {
    throw RuntimeException("Failed to load bot token. Make sure to create a file named bot-token.txt in" +
            " src/main/resources and paste the bot token into that file.", error)
}

suspend fun main(args: Array<String>) {
    bot(BOT_TOKEN) {
        commands("-=") {
            command("test") {
                reply {
                    title = "test"
                    description = "성공"
                }
            }
            command("req") {
                if(this.channelId.equals("759308637385654323")) {
                    reply {
                        title = "새로운 건의가 추가되었습니다!"
                        description = words.drop(1).joinToString(" ")
                    }
                }
            }
            command("adr") {
                reply{
                    title = "서버 주소"
                    description = "multiground.kro.kr:25560"
                }
            }
            command("?") {
                reply{
                    title = "멀그봇 명령어 목록"
                    description = "-=?: 명령어 목록을 표시합니다.\n-=req: 건의채널에서 건의를 보냅니다.\n-=adr: 멀그 주소를 보여줍니다.\n"
                }
            }
            command("reqacc") {
                if(this.channelId.equals("759308637385654323")){
                    if(this.authorId.equals("364758752051855360")){
                        reply{
                            title = "건의가 수락되었습니다!"
                            description = words.drop(1).joinToString(" ")
                        }
                    }
                }
            }
            command("reqden") {
                if(this.channelId.equals("759308637385654323")){
                    if(this.authorId.equals("364758752051855360")){
                        reply{
                            title = "건의가 취소되었습니다!"
                            description = words.drop(1).joinToString(" ")
                        }
                    }
                }
            }

        }
        messageCreated {
            if (it.content.contains("투표:")) {
                it.react("✅")
                it.react("❌")
            }
            else if (it.content.contains("모집:")){
                it.react("✋")
            }
            if(it.channelId.equals("759308637385654323")){
                if (!it.authorId.equals("777135853608370217")){
                    it.delete()
                }
            }
            if(it.channelId.equals("759364798516953099")){
                if (it.attachments.isEmpty()){
                    it.delete()
                }
            }
        }
    }
}

