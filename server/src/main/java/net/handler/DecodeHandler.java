package net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import net.impl.SecTransferServer;

import java.nio.charset.Charset;

public class DecodeHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public void channelRead0(ChannelHandlerContext ctx,ByteBuf in){

        String cypher=ctx.channel().attr(SecTransferServer.CypherKey).get();

        byte[] packetCyher=new byte[8];

    }


}
