package main

import (
	_ "af-askari/internal/packed"

	_ "af-askari/internal/logic"

	_ "af-askari/internal/logic/logic.go"

	"github.com/gogf/gf/v2/os/gctx"

	"af-askari/internal/cmd"
)

func main() {
	cmd.Main.Run(gctx.GetInitCtx())
}
