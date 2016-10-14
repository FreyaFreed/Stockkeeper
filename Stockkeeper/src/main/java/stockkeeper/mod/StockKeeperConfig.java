package stockkeeper.mod;

public class StockKeeperConfig {

	public static String stockkeeperIp;
	public static String password;
	public static String defaultGroup;
	public static boolean savePassword;
	public static String getDefaultGroup() {
		return defaultGroup;
	}
	public static String getPassword() {
		return password;
	}
	public static boolean getSavePassword() {
		return savePassword;
	}
	public static String getStockkeeperIp() {
		return stockkeeperIp;
	}

	public static StockKeeperConfig Instance()
	{
		return new StockKeeperConfig();
	}

}
