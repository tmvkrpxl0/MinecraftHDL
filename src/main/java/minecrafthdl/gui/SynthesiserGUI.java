package minecrafthdl.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import minecrafthdl.block.blocks.Synthesizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Francis on 3/25/2017.
 */
public class SynthesiserGUI extends Screen {

    Button synthesize_button, up_button, down_button;

    String file_directory = "./verilog_designs";

    ArrayList<String> file_names = new ArrayList<String>();
    int selected_file = -1;


    int window_width = 256;
    int window_height = 256;

    int window_left, window_top, filebox_left, filebox_top, filebox_right, filebox_bottom;

    int line_height = 10;
    int padding = 2;
    int total_height = line_height + (2 * padding);

    int start_file_index = 0;

    BlockPos blockPos;
    Level world;

    List<Button> buttonList = new LinkedList<>();


    public SynthesiserGUI(Level world, BlockPos blockPos) {
        super(Component.literal("Minecraft HDL"));
        this.world = world;
        this.blockPos = blockPos;
    }


    @Override
    public void init() {
        this.window_left = centerObjectTL(this.window_width, this.width);
        this.window_top = centerObjectTL(this.window_height, this.height);

        this.filebox_left = window_left + 12;
        this.filebox_right = window_left + 150;
        this.filebox_top = window_top + 25;
        this.filebox_bottom = window_top + 130;

        this.buttonList.add(this.synthesize_button = new Button(this.width / 2 - 50, this.height / 2 + 52, 100, 20, Component.literal("Generate Design"), button -> {
            if (this.selected_file < 0) {
                this.minecraft.setScreen(null);
                if (this.minecraft.screen == null)
                    this.minecraft.mouseHandler.grabMouse();
            }

            Synthesizer.file_to_gen = this.file_directory + "/" + this.file_names.get(this.selected_file);

            this.minecraft.setScreen(null);
            if (this.minecraft.screen == null)
                this.minecraft.mouseHandler.grabMouse();
        }));
        this.buttonList.add(this.up_button = new Button(this.filebox_right + 1, this.filebox_top - 1, 20, 20, Component.literal("^"), button -> {
            if (this.start_file_index > 0) this.start_file_index-= 1;
        }));
        this.buttonList.add(this.down_button = new Button(this.filebox_right + 1, this.filebox_bottom - 19, 20, 20, Component.literal("/"), button -> {
            if (this.start_file_index + 6 < this.file_names.size() - 1) this.start_file_index += 1;
        }));


        System.out.println("Win L: " + this.window_left + "\tWin T: " + this.window_top);

        this.synthesize_button.active = false;
        this.file_names = this.readFileNames();
    }

    private ArrayList<String> readFileNames(){
        ArrayList<String> files = new ArrayList<String>();
        File folder = new File(file_directory);

        System.out.println("PWD: " + System.getProperty("user.dir"));

        if (!folder.exists()) {
            folder.mkdir();
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Created folder 'verilog_designs'"));
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Copy your synthesized JSON files to this directory:"));
            Minecraft.getInstance().player.sendSystemMessage(Component.literal(System.getProperty("user.dir") + "\\verilog_designs"));

        } else {
            for (File f : folder.listFiles()){
                if (f.getName().toLowerCase().endsWith(".json")) {
                    files.add(f.getName());
                }
            }
        }

        return files;
    }

    private int centerObjectTL(int obj_dimension, int scrn_dimension){
        return (scrn_dimension / 2) - (obj_dimension / 3);
    }

    private int centerObjectBR(int obj_dimension, int scrn_dimension){
        return (scrn_dimension / 2) + (obj_dimension / 2);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        this.minecraft.getTextureManager().bindTexture(new ResourceLocation("minecrafthdl:textures/gui/synthesiser.png"));
        this.drawTexturedModalRect(centerObjectTL(this.window_width, this.width), centerObjectTL(this.window_height, this.height), 0, 0, this.window_width, this.window_height);

        this.fontRendererObj.drawString(
                "Synthesiser",
                (this.width / 2) - (this.fontRendererObj.getStringWidth("Synthesiser") / 2),
                (this.height / 2) - 75,
                0
        );

        this.drawFileNames();

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void drawFileNames(){
        int current_height = this.filebox_top;
        int files_shown = 0;
        for (int i = this.start_file_index; i < this.file_names.size(); i++) {
            if (files_shown == 7) break;
            else  files_shown++;
            String file_name = this.file_names.get(i);
            int max_width = this.filebox_right - this.filebox_left - (2 * this.padding);
            if (this.fontRendererObj.getStringWidth(file_name) > max_width) {
                file_name = this.fontRendererObj.trimStringToWidth(file_name, max_width - this.fontRendererObj.getStringWidth("...")) + "...";
            }

            if (this.selected_file == i){

                this.drawGradientRect(
                        this.filebox_left,
                        current_height,
                        this.filebox_right,
                        current_height + this.total_height,
                        0xFFFFFFFF, 0xFFFFFFFF
                );

                current_height += this.padding;

                this.fontRendererObj.drawString(
                        file_name,
                        this.filebox_left + this.padding,
                        current_height,
                        0x00000000
                );

                current_height += this.line_height + this.padding;

            } else {
                current_height += this.padding;

                this.fontRendererObj.drawString(
                        file_name,
                        this.filebox_left + this.padding,
                        current_height,
                        0xFFFFFFFF
                );

                current_height += this.line_height + this.padding;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
        if(mouseX >= this.filebox_left && mouseX <= this.filebox_right && mouseY >= this.filebox_top && mouseY <= this.filebox_bottom) {
            int index = (mouseY - this.filebox_top + (this.start_file_index * this.line_height)) / this.total_height;
            if (index < this.file_names.size()){
                this.selected_file = index;
                this.synthesize_button.active = true;
            } else {
                this.selected_file = -1;
                this.synthesize_button.active = false;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
