package com.binaris.extracmds.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

// Silly warnings... o-o
@SuppressWarnings("NullableProblems")
public class ReplaceLoreLineCommand extends CommandBase {
    @Override
    public String getName() {
        return "replaceloreline";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.extracmds.setloreline.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] strings) throws CommandException {
        EntityPlayerMP player = null;

        try {
            player = getCommandSenderAsPlayer(sender);
        } catch (PlayerNotFoundException exception) {
            if (server.sendCommandFeedback()) {
                ITextComponent textComponent = new TextComponentString("You must be a player to use this command.");
                textComponent.getStyle().setColor(TextFormatting.RED);
                sender.sendMessage(textComponent);
                return;
            }
        }

        ItemStack itemStack = player.getHeldItemMainhand();

        if (itemStack.isEmpty()) {
            if (server.sendCommandFeedback()) {
                ITextComponent textComponent = new TextComponentString("You must be holding an item to use this command.");
                textComponent.getStyle().setColor(TextFormatting.RED);
                sender.sendMessage(textComponent);
                return;
            }
        }

        if (strings.length < 2) {
            if (server.sendCommandFeedback()) {
                ITextComponent textComponent = new TextComponentString("You must provide a line number and the new lore line.");
                textComponent.getStyle().setColor(TextFormatting.RED);
                sender.sendMessage(textComponent);
            }
            return;
        }

        int lineNumber;
        try {
            lineNumber = Integer.parseInt(strings[0]) - 1; // Convert to zero-indexed
        } catch (NumberFormatException e) {
            ITextComponent textComponent = new TextComponentString("The first argument must be a number.");
            textComponent.getStyle().setColor(TextFormatting.RED);
            sender.sendMessage(textComponent);
            return;
        }

        StringBuilder loreLine = new StringBuilder();
        for (int i = 1; i < strings.length; i++) {
            loreLine.append(strings[i]).append(" ");
        }
        loreLine.deleteCharAt(loreLine.length() - 1);

        // Replace all the & with §
        loreLine = new StringBuilder(loreLine.toString().replace('&', '\u00A7'));

        NBTTagCompound displayTag = itemStack.getOrCreateSubCompound("display");
        NBTTagList loreList = displayTag.getTagList("Lore", 8);

        if (lineNumber < 0 || lineNumber >= loreList.tagCount()) {
            ITextComponent textComponent = new TextComponentString("Invalid line number.");
            textComponent.getStyle().setColor(TextFormatting.RED);
            sender.sendMessage(textComponent);
            return;
        }

        loreList.set(lineNumber, new NBTTagString(loreLine.toString()));
        displayTag.setTag("Lore", loreList);

        if (server.sendCommandFeedback()) {
            ITextComponent textComponent = new TextComponentString("Set lore line " + (lineNumber + 1) + " to: " + loreLine);
            textComponent.getStyle().setColor(TextFormatting.GREEN);
            sender.sendMessage(textComponent);
        }
    }
}
