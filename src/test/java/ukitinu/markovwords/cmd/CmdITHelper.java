package ukitinu.markovwords.cmd;

import ukitinu.markovwords.repo.FileRepo;
import ukitinu.markovwords.repo.Repo;

import java.io.ByteArrayOutputStream;

sealed abstract class CmdITHelper permits DeleteCmdIT, InfoCmdIT, ListCmdIT, UpdateCmdIT,
        RestoreCmdIT, CreateCmdIT, ReadCmdIT, WriteCmdIT {
    final String basePath = "./src/test/resources/dict_dir";
    final Repo repo = FileRepo.create(basePath);
    final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
}
