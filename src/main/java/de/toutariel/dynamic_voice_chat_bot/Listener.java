package de.toutariel.dynamic_voice_chat_bot;


import net.dv8tion.jda.api.entities.Guild;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Listener extends ListenerAdapter {

    private final Logger LOGGER = DynamicVoiceChatBot.dynamicVoiceChatBot.LOGGER;

    private HashMap<String,Integer> safe = new HashMap<>();//List has to be sortet

    private final String CATEGORIE_NAME = "Dynamic Voice Chats";
    private final String VOICE_NAME = "Dynamic Voice ";

    private final Pattern PATTERN = Pattern.compile("^"+ VOICE_NAME +"(\\d+)$");


    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for (Guild guild: event.getJDA().getGuilds()) {
            var spaces = guild.getCategoriesByName(CATEGORIE_NAME, true);
            if (spaces.isEmpty()) spaces.add(guild.createCategory(CATEGORIE_NAME).complete());
            for (var categorie: spaces){

                for (var vc: categorie.getVoiceChannels()){
                    if(vc.getName().matches(PATTERN.pattern())) vc.delete().complete();
                }

                categorie.createVoiceChannel(VOICE_NAME +"0").complete();
                safe.put(guild.getId()+categorie.getId(), 0);
            }
        }
    }

    private void vcJoin(VoiceChannel channelJoined){
        LOGGER.info("Join");
        var categorie = channelJoined.getParent();
        if (categorie == null) return;
        var m = PATTERN.matcher(channelJoined.getName());
        if (!m.find()) return;
        var tmp = m.group(1);
        int num = Integer.parseInt(tmp);
        int channel_num = safe.get(channelJoined.getGuild().getId() + categorie.getId());

        if (channel_num <= num){
            channel_num++;
            safe.replace(channelJoined.getGuild().getId() + categorie.getId(), channel_num);
            categorie.createVoiceChannel(VOICE_NAME +channel_num).complete();
        }
        LOGGER.info(String.valueOf(safe.get(channelJoined.getGuild().getId() + categorie.getId())));
    }

    private void vcLeave(VoiceChannel channelLeft){
        LOGGER.info("leave");
        if (channelLeft == null){
            System.out.println("Problem voice channel nicht gefunden");
            return;
        }
        if (!Objects.requireNonNull(channelLeft).getMembers().isEmpty()) return;
        var categorie = Objects.requireNonNull(channelLeft).getParent();
        if (categorie == null) return;
        var m = PATTERN.matcher(channelLeft.getName());
        if (!m.find()) return;
        int num = Integer.parseInt(m.group(1));
        var channel_num = safe.get(channelLeft.getGuild().getId() + categorie.getId());

        if (num == channel_num) return;
        channelLeft.delete().complete();
        safe.replace(channelLeft.getGuild().getId() + categorie.getId(), channel_num-1);

        for (var vc: categorie.getVoiceChannels()){
            var mc = PATTERN.matcher(vc.getName());
            if (!mc.find()) continue;
            int numc = Integer.parseInt(mc.group(1));

            if (numc > num) {
                int new_num = numc - 1;
                vc.getManager().setName(VOICE_NAME + new_num).complete();
            }
        }
        LOGGER.info(String.valueOf(safe.get(channelLeft.getGuild().getId() + categorie.getId())));
    }
    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        VoiceChannel channelJoined = event.getChannelJoined();
        vcJoin(channelJoined);

    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {

        VoiceChannel channelLeft = event.getChannelLeft();
        vcLeave(channelLeft);
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        vcLeave(event.getChannelLeft());
        vcJoin(event.getChannelJoined());
    }
}
