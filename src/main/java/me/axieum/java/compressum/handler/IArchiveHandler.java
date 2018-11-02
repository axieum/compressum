package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.Compressum;

import java.io.File;

public interface IArchiveHandler
{
    File serialize(Compressum compressum);
}
