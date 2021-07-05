package protocol;

public interface TcpRecvParser {
	
	/**
	 * 
	 * @param 
	 * @return Первое возвращаемое значение - true или false, указывающее, анализируется ли пакет
         * Второе возвращаемое значение - это фактическая длина сообщения, bs.length может быть больше, 
         * чем фактическая длина сообщения.
	 */
	public Object[] parseRevContent(byte[] bs);
}