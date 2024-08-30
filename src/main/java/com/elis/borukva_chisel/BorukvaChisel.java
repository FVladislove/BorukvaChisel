package com.elis.borukva_chisel;

import com.elis.borukva_chisel.block.ModBlocks;
import com.elis.borukva_chisel.item.ModItems;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BorukvaChisel implements ModInitializer {
	public final static String MOD_ID = "borukva_chisel";
    public static final Logger logger = LoggerFactory.getLogger(BorukvaChisel.class);

	@Override
	public void onInitialize() {
		logger.info("Mod initializing");

		ModItems.register();
		ModBlocks.register();


		// to provide a resource pack for clients, you must first generate it
		// using the command "/polymer generate-pack" (in the game or server console)
		// to update, you must re-login
		if (PolymerResourcePackUtils.addModAssets(MOD_ID)) {
			logger.info("Successfully added mod assets for " + MOD_ID);
		} else {
			logger.info("Failed to add mod assets " + MOD_ID);
		}


		PolymerResourcePackUtils.markAsRequired();
	}
}