package stockkeeper.service;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import org.lwjgl.opengl.GL11;


public class RenderService extends Gui {

	private Minecraft mc = Minecraft.getMinecraft();
	private Color radarColor;
	private double pingDelay = 63.0D;
	private List entityList;
	private float radarScale;
	ArrayList<String> inRangePlayers;
	ArrayList<Waypoint> waypoints;
	
	public RenderService() {
		inRangePlayers = new ArrayList<String>();
		waypoints = new ArrayList<Waypoint>();
	}
	
	@SubscribeEvent
	public void renderRadar(RenderGameOverlayEvent event) {
	
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.START && mc.theWorld != null) {
		}
	}
	
	public void AddWaypoint(int x, int y, int z, String name){
		Waypoint point = new Waypoint(x, y, z, name, new Color(255, 255, 255), true);
		waypoints.add(point);
		
	}
	
	@SubscribeEvent
	public void renderWaypoints(RenderWorldLastEvent event) {			
			for(Waypoint point : waypoints) {
				if(point.getDimension() == mc.theWorld.provider.getDimension() && point.isEnabled()) {
					renderWaypoint(point, event);
					}
			}
		
	}
	
	private void drawCircle(int x, int y, double radius, Color c, boolean filled) {
		GL11.glEnable(3042);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(2848);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, 0.5F);
		GL11.glBegin(filled ? 6 : 2);
		for (int i = 0; i <= 360; i++) {
			double x2 = Math.sin(i * Math.PI / 180.0D) * radius;
			double y2 = Math.cos(i * Math.PI / 180.0D) * radius;
			GL11.glVertex2d(x + x2, y + y2);
		}
		GL11.glEnd();
		GL11.glDisable(2848);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(3042);
	}
	
	private void drawTriangle(int x, int y, Color c) {
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glColor4f(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, 1.0F);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		GL11.glBlendFunc(770, 771);
		GL11.glBegin(4);
		GL11.glVertex2d(x, y + 3);
		GL11.glVertex2d(x + 3, y - 3);
		GL11.glVertex2d(x - 3, y - 3);
		GL11.glEnd();
		GL11.glDisable(2848);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glRotatef(-180.0F, 0.0F, 0.0F, 1.0F);
	}
	
	
	private void renderWaypoint(Waypoint point, RenderWorldLastEvent event) {
		String name = point.getName();
		Color c = point.getColor();
		float partialTickTime = event.getPartialTicks();
		double distance = point.getDistance(mc);
		if(distance <= 2000 || 2000 < 0) {
			FontRenderer fr = mc.fontRendererObj;
			Tessellator tess = Tessellator.getInstance();
			VertexBuffer wr = tess.getBuffer();
			RenderManager rm = mc.getRenderManager();
			
			float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
			float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
			float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);
			
			float displayX = (float)point.getX() - playerX;
			float displayY = (float)point.getY() + 1.3f - playerY;
			float displayZ = (float)point.getZ() - playerZ;
			
			float scale = (float) (Math.max(2, distance /5) * 0.0185f);
			
			GL11.glColor4f(1f, 1f, 1f, 1f);
			GL11.glPushMatrix();
			GL11.glTranslatef(displayX, displayY, displayZ);
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-scale, -scale, scale);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			name += " (" + (int)distance + "m)";
			int width = fr.getStringWidth(name);
			int height = 10;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);	
			int stringMiddle = width / 2;
			wr.color(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, 0.5f);
			wr.addVertexData(new int[]{-stringMiddle - 1, -1, 0});
			wr.addVertexData(new int[]{-stringMiddle - 1, 1 + height, 0});
			wr.addVertexData(new int[]{stringMiddle + 1, 1 + height, 0});
			wr.addVertexData(new int[]{stringMiddle + 1,  -1, 0});
			tess.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			fr.drawString(name, -width / 2, 1, Color.WHITE.getRGB());
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glPopMatrix();
		}
	}
}