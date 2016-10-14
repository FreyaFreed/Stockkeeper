/**
 *
 */
package stockkeeper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author Freya
 *
 */
public class StockkeeperCommand extends CommandBase {

	private void commandNotFound() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.minecraft.command.ICommand#execute(net.minecraft.server.MinecraftServer, net.minecraft.command.ICommandSender, java.lang.String[])
	 */
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {


	}

	/* (non-Javadoc)
	 * @see net.minecraft.command.ICommand#getCommandName()
	 */
	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "stock";
	}
	/* (non-Javadoc)
	 * @see net.minecraft.command.ICommand#getCommandUsage(net.minecraft.command.ICommandSender)
	 */
	@Override
	public String getCommandUsage(ICommandSender sender) {
		String newLine = System.getProperty("line.separator");
		String commandUsage = ""
				+ "Count: counts the amount of the specific item in all chests that the use has access to" + newLine
				+ " Usage: '/stock count [itemName]' Spaces in items are represented with '_', not case senstive" + newLine
				+ "Find: finds the closest chest containing the item"
				+ " Usage: '/stock find [itemName]' Spaces in items are represented with '_', not case senstive "
				+ ""
				+ ""
				+ ""
				+ "";
		return "TODO";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	private void handleAdd(String[] args) {
		// TODO Auto-generated method stub

	}

	private void handleCount(String[] args) {
		// TODO Auto-generated method stub

	}

	private void handleFind(String[] args) {
		// TODO Auto-generated method stub

	}

	private void handleInvite(String[] args) {
		// TODO Auto-generated method stub

	}

}
