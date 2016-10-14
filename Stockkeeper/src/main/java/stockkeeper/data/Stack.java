package stockkeeper.data;

import java.io.Serializable;

public class Stack implements Serializable{
	public String name;
	public int size;
	public Stack(String name_, int size_)
	{
		name = name_;
		size = size_;

	}

}
