package com.ziplinegames.moai;

public class MoaiLuaBridge
{
    protected static native void AKUMoaiLuaBridgeCallLuaFunctionWithString(int luaFunctionId, String value);
    protected static native void AKUMoaiLuaBridgeCallLuaGlobalFunctionWithString(String luaFunctionName, String value);
    protected static native void AKUMoaiLuaBridgeRetainLuaFunction(int luaFunctionId);
    protected static native void AKUMoaiLuaBridgeReleaseLuaFunction(int luaFunctionId);
    
    public static void callLuaFunctionWithString(int luaFunctionId, String value)
    {
    	synchronized(Moai.sAkuLock)
    	{
    		AKUMoaiLuaBridgeCallLuaFunctionWithString(luaFunctionId, value);
    	}
    }
    
    public static void callLuaGlobalFunctionWithString(String luaFunctionName, String value)
    {
    	synchronized(Moai.sAkuLock)
    	{
    		AKUMoaiLuaBridgeCallLuaGlobalFunctionWithString(luaFunctionName, value);
    	}
    }
    
    public static void retainLuaFunction(int luaFunctionId)
    {
    	synchronized(Moai.sAkuLock)
    	{
    		AKUMoaiLuaBridgeRetainLuaFunction(luaFunctionId);
    	}
    }
    
    public static void releaseLuaFunction(int luaFunctionId)
    {
    	synchronized(Moai.sAkuLock)
    	{
    		AKUMoaiLuaBridgeReleaseLuaFunction(luaFunctionId);
    	}
    }
}

