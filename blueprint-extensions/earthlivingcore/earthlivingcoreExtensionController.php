<?php

namespace Pterodactyl\Http\Controllers\Admin\Extensions\earthlivingcore;

use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\View\Factory as ViewFactory;
use Illuminate\View\View;
use Pterodactyl\BlueprintFramework\Libraries\ExtensionLibrary\Admin\BlueprintAdminLibrary as BlueprintExtensionLibrary;
use Pterodactyl\Http\Controllers\Controller;
use Pterodactyl\Services\Helpers\SoftwareVersionService;

class earthlivingcoreExtensionController extends Controller
{
    public function __construct(
        private BlueprintExtensionLibrary $blueprint,
        private SoftwareVersionService $version,
        private ViewFactory $view
    ) {}

    public function index(): View
    {
        $rootPath = '/admin/extensions/earthlivingcore';

        return $this->view->make('admin.extensions.earthlivingcore.index', [
            'blueprint' => $this->blueprint,
            'version' => $this->version,
            'root' => $rootPath,
        ]);
    }

    public function post(Request $request): RedirectResponse
    {
        $allowedActions = ['open', 'repair-approved', 'completed'];
        $action = (string) $request->input('earthliving_report_action', '');
        $reportId = (string) $request->input('earthliving_report_id', '');
        $note = (string) $request->input('earthliving_report_note', '');
        $queuePath = '/var/lib/pterodactyl/volumes/0157164c-e4e1-4979-935c-d703ddd6706e/plugins/EarthLivingCore/reports-actions.queue';
        $staffUser = $request->user();
        $staffName = $staffUser->username ?? $staffUser->email ?? 'Panel';

        if (!in_array($action, $allowedActions, true) || !preg_match('/^\d+$/', $reportId) || !is_writable($queuePath)) {
            return redirect('/admin/extensions/earthlivingcore?view=reports')
                ->with('earthlivingReportActionError', 'Report action could not be queued. Check action, report id, or queue file permissions.');
        }

        $queueLine = implode('|', [
            now()->toIso8601String(),
            bin2hex(random_bytes(8)),
            $reportId,
            $action,
            base64_encode($staffName),
            base64_encode($note),
        ]) . PHP_EOL;

        file_put_contents($queuePath, $queueLine, FILE_APPEND | LOCK_EX);
        usleep(3500000);

        return redirect('/admin/extensions/earthlivingcore?view=reports')
            ->with('earthlivingReportAction', 'Report #' . $reportId . ' queued as ' . $action . '.');
    }
}
