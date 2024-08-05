package com.elis.borukva_chisel;

import com.elis.borukva_chisel.item.ModItems;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BorukvaChisel implements ModInitializer {
	public final static String MOD_ID = "borukva_chisel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Mod initializing");

		ModItems.register();
		PolymerResourcePackUtils.markAsRequired();

		// to provide a resource pack for clients, you must first generate it
		// using the command "/polymer generate-pack" (in the game or server console)
		// to update, you must re-login
		if (PolymerResourcePackUtils.addModAssets(MOD_ID)) {
			LOGGER.info("Successfully added mod assets for " + MOD_ID);
		} else {
			LOGGER.info("Failed to add mod assets " + MOD_ID);
		}
	}
}