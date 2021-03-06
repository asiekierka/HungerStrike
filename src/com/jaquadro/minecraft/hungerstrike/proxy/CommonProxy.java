package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.ExtendedPlayerProvider;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.jaquadro.minecraft.hungerstrike.network.RequestSyncMessage;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy
{

    public PlayerHandler playerHandler;

    public CommonProxy () {
        playerHandler = new PlayerHandler();
    }

    public void registerNetworkHandlers () {
        HungerStrike.network.registerMessage(RequestSyncMessage.Handler.class, RequestSyncMessage.class, RequestSyncMessage.MESSAGE_ID, Side.SERVER);
    }

    @SubscribeEvent
    public void tick (TickEvent.PlayerTickEvent event) {
        playerHandler.tick(event.player, event.phase, event.side);
    }

    @SubscribeEvent
    public void attachCapabilites (AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(ExtendedPlayer.EXTENDED_PLAYER_KEY, new ExtendedPlayerProvider((EntityPlayer) event.getObject()));
    }

    @SubscribeEvent
    public void livingDeath (LivingDeathEvent event) {
        if (!event.getEntity().worldObj.isRemote && event.getEntity() instanceof EntityPlayerMP)
            playerHandler.storeData((EntityPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void entityJoinWorld (EntityJoinWorldEvent event) {
        if (!event.getEntity().worldObj.isRemote && event.getEntity() instanceof EntityPlayerMP)
            playerHandler.restoreData((EntityPlayer) event.getEntity());
        else if (event.getEntity().worldObj.isRemote && event.getEntity() instanceof EntityPlayerSP)
            HungerStrike.network.sendToServer(new RequestSyncMessage());
    }
}
