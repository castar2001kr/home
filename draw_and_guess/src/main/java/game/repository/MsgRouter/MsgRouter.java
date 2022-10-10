package game.repository.MsgRouter;

import game.repository.ActRouter.ActEntrance;

public interface MsgRouter {

	void submit(String msg, int pid);
	
}
