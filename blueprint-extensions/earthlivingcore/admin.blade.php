<link rel="stylesheet" href="/assets/extensions/earthlivingcore/admin.style.css?v=20260519-reports-fix">
<style>
    .earthliving-extension-page {
        color: #edf3ec;
        background: #0b1110;
        padding: 18px;
        min-height: 100vh;
    }

    .earthliving-extension-page h2,
    .earthliving-extension-page h3,
    .earthliving-extension-page h4,
    .earthliving-extension-page strong {
        color: #ffffff;
    }

    .earthliving-extension-page p,
    .earthliving-extension-page span,
    .earthliving-extension-page small {
        color: #cbd8cb;
    }
</style>

@php
    $marketplaceSections = [
        [
            'title' => 'Pterodactyl Themes',
            'icon' => 'fa-paint-brush',
            'summary' => 'Visual upgrades for the panel. Use Blueprint-compatible themes first so updates stay safer.',
            'items' => [
                ['name' => 'Euphoria Theme', 'tag' => 'Research', 'risk' => 'Medium', 'url' => 'https://euphoriatheme.uk/', 'note' => 'Blueprint-powered theme. Compare against Earth Living carbon style before installing.'],
                ['name' => 'Blueprint Night Theme', 'tag' => 'Optional', 'risk' => 'Low', 'url' => 'https://www.sourcexchange.net/products/blueprint-night-theme', 'note' => 'Free dark admin theme idea source. We already have a custom carbon theme.'],
                ['name' => 'Custom Earth Living Core', 'tag' => 'Active', 'risk' => 'Low', 'url' => route('admin.extensions'), 'note' => 'Current local Blueprint extension controlling branding, buttons, cards and owner shortcuts.'],
            ],
        ],
        [
            'title' => 'Plugin Installers',
            'icon' => 'fa-download',
            'summary' => 'Tools for browsing and staging Minecraft plugins without installing directly into production.',
            'items' => [
                ['name' => 'Earth Living Plugin Gate', 'tag' => 'Active', 'risk' => 'Low', 'url' => route('admin.plugins'), 'note' => 'Current staging-first workflow for Spigot research and test-server approval.'],
                ['name' => 'Minecraft Plugin Manager', 'tag' => 'Research', 'risk' => 'High', 'url' => 'https://www.sourcexchange.net/products/minecraft-plugin-manager-for-pterodactyl', 'note' => 'One-click plugin installs are useful, but must be adapted to staging/test approval first.'],
                ['name' => 'Modrinth/Hangar/Spigot Browser', 'tag' => 'Planned', 'risk' => 'Medium', 'url' => route('admin.plugins'), 'note' => 'Future unified search with compatibility notes for Paper 26.1.2 and Java 25.'],
            ],
        ],
        [
            'title' => 'Player Managers',
            'icon' => 'fa-users',
            'summary' => 'Owner tools for whitelist, roles, punishments, reports and player lookup.',
            'items' => [
                ['name' => 'Whitelist Manager', 'tag' => 'Planned', 'risk' => 'Low', 'url' => route('admin.users'), 'note' => 'Add/remove test players and later sync with Discord verification.'],
                ['name' => 'LuckPerms Role Overview', 'tag' => 'Planned', 'risk' => 'Medium', 'url' => route('admin.servers'), 'note' => 'Read-only owner dashboard first, then safe role actions later.'],
                ['name' => 'Player Reports Dashboard', 'tag' => 'Planned', 'risk' => 'Medium', 'url' => route('admin.plugins'), 'note' => 'Moderation queue for chat, griefing, bugs and staff alerts.'],
            ],
        ],
        [
            'title' => 'Admin Tools',
            'icon' => 'fa-wrench',
            'summary' => 'Operational tools that help keep Earth Living stable before public launch.',
            'items' => [
                ['name' => 'Backup Monitor', 'tag' => 'Recommended', 'risk' => 'Low', 'url' => route('admin.servers'), 'note' => 'Show latest backup age, size and failed backup warnings.'],
                ['name' => 'BlueMap Status', 'tag' => 'Recommended', 'risk' => 'Low', 'url' => route('admin.servers'), 'note' => 'Show render state, web status, disk usage and map links.'],
                ['name' => 'Velocity Network View', 'tag' => 'In Progress', 'risk' => 'Low', 'url' => route('admin.servers'), 'note' => 'Visual overview of Velocity, Earth Living, Standard Survival and test server routing.'],
            ],
        ],
        [
            'title' => 'External Ecosystem',
            'icon' => 'fa-external-link',
            'summary' => 'Places to research addons. Anything from here should be reviewed before it touches production.',
            'items' => [
                ['name' => 'Blueprint Framework', 'tag' => 'Trusted base', 'risk' => 'Low', 'url' => 'https://github.com/BlueprintFramework/framework', 'note' => 'Framework already used for this panel customization.'],
                ['name' => 'Euphoria Development', 'tag' => 'Research', 'risk' => 'Medium', 'url' => 'https://euphoriadevelopment.uk/index.html', 'note' => 'Blueprint themes and extensions for Pterodactyl. Review compatibility first.'],
                ['name' => 'SourceXchange Pterodactyl Addons', 'tag' => 'Research', 'risk' => 'High', 'url' => 'https://www.sourcexchange.net/', 'note' => 'Marketplace with paid addons. Treat as research until tested in staging.'],
            ],
        ],
    ];

    $reportSources = [
        'Test Server' => '/var/lib/pterodactyl/volumes/d554d2b4-ac4b-4b48-b004-35f1b73feadc/plugins/EarthLivingCore/reports-panel.json',
        'Main Server' => '/var/lib/pterodactyl/volumes/0157164c-e4e1-4979-935c-d703ddd6706e/plugins/EarthLivingCore/reports-panel.json',
    ];

    $reportSourceName = 'Not connected';
    $reportSourcePath = null;
    $reportData = ['generatedAt' => null, 'openCount' => 0, 'reports' => []];

    foreach ($reportSources as $sourceName => $sourcePath) {
        if (is_readable($sourcePath)) {
            $decodedReports = json_decode(file_get_contents($sourcePath), true);
            if (is_array($decodedReports)) {
                $reportSourceName = $sourceName;
                $reportSourcePath = $sourcePath;
                $reportData = array_merge($reportData, $decodedReports);
                break;
            }
        }
    }

    $panelReports = array_slice($reportData['reports'] ?? [], 0, 8);
@endphp

<div class="earthliving-extension-page earthliving-marketplace-page">
    <section class="earthliving-extension-hero earthliving-marketplace-hero">
        <div>
            <span class="earthliving-kicker">Owner toolkit</span>
            <h2>Panel Marketplace</h2>
            <p>
                Curated Pterodactyl themes, plugin installers, player managers, admin tools and network utilities for Earth Living.
            </p>
        </div>
        <div class="earthliving-extension-actions">
            <a class="btn btn-success" href="#earthliving-report-center">
                <i class="fa fa-flag"></i> Report Center
            </a>
            <a class="btn btn-primary" href="{{ route('admin.plugins') }}">
                <i class="fa fa-puzzle-piece"></i> Plugin Library
            </a>
            <a class="btn btn-default" href="{{ route('admin.servers') }}">
                <i class="fa fa-server"></i> Servers
            </a>
        </div>
    </section>

    <div class="earthliving-policy-strip">
        <div>
            <span>Install policy</span>
            <strong>Research -> staging -> backup -> production</strong>
        </div>
        <div>
            <span>Runtime target</span>
            <strong>Paper 26.1.2 build 64 + Java 25</strong>
        </div>
        <div>
            <span>Network plan</span>
            <strong>Velocity -> Earth Living / Survival / Test</strong>
        </div>
    </div>

    <section id="earthliving-report-center" class="earthliving-report-center">
        <header>
            <div>
                <span class="earthliving-kicker">Read-only alpha</span>
                <h3>Report Center</h3>
                <p>Minecraft reports exported by EarthLivingCore. Panel actions come later after the read-only flow is stable.</p>
            </div>
            <div class="earthliving-report-stats">
                <div>
                    <span>Source</span>
                    <strong>{{ $reportSourceName }}</strong>
                </div>
                <div>
                    <span>Open</span>
                    <strong>{{ $reportData['openCount'] ?? 0 }}</strong>
                </div>
                <div>
                    <span>Updated</span>
                    <strong>{{ $reportData['generatedAt'] ?? 'No export yet' }}</strong>
                </div>
            </div>
        </header>

        @if (count($panelReports) === 0)
            <article class="earthliving-report-empty">
                <i class="fa fa-inbox"></i>
                <div>
                    <h4>No reports exported yet</h4>
                    <p>Create a report in EarthOS on the test server, then refresh this panel page.</p>
                    @if ($reportSourcePath)
                        <small>{{ $reportSourcePath }}</small>
                    @endif
                </div>
            </article>
        @else
            <div class="earthliving-report-list">
                @foreach ($panelReports as $report)
                    <article class="earthliving-report-card">
                        <div class="earthliving-report-card-head">
                            <strong>#{{ $report['id'] ?? '?' }} {{ $report['categoryTitle'] ?? 'Report' }}</strong>
                            <span>{{ $report['status'] ?? 'unknown' }}</span>
                        </div>
                        <p>{{ $report['note'] ?? '' }}</p>
                        <div class="earthliving-report-meta">
                            <span><i class="fa fa-user"></i> {{ $report['playerName'] ?? 'Unknown' }}</span>
                            <span><i class="fa fa-map-marker"></i> {{ $report['world'] ?? 'world' }} {{ $report['x'] ?? 0 }} {{ $report['y'] ?? 0 }} {{ $report['z'] ?? 0 }}</span>
                            <span><i class="fa fa-clock-o"></i> {{ $report['createdAt'] ?? 'unknown' }}</span>
                        </div>
                    </article>
                @endforeach
            </div>
        @endif
    </section>

    <div class="row earthliving-market-grid">
        @foreach ($marketplaceSections as $section)
            <div class="col-md-6">
                <section class="earthliving-market-section">
                    <header>
                        <i class="fa {{ $section['icon'] }}"></i>
                        <div>
                            <h3>{{ $section['title'] }}</h3>
                            <p>{{ $section['summary'] }}</p>
                        </div>
                    </header>

                    @foreach ($section['items'] as $item)
                        <article class="earthliving-market-item">
                            <div>
                                <h4>{{ $item['name'] }}</h4>
                                <p>{{ $item['note'] }}</p>
                            </div>
                            <div class="earthliving-market-meta">
                                <span class="earthliving-badge">{{ $item['tag'] }}</span>
                                <span class="earthliving-risk earthliving-risk-{{ strtolower($item['risk']) }}">{{ $item['risk'] }} risk</span>
                                <a class="btn btn-default btn-xs" href="{{ $item['url'] }}" target="_blank" rel="noreferrer">
                                    <i class="fa fa-external-link"></i> Open
                                </a>
                            </div>
                        </article>
                    @endforeach
                </section>
            </div>
        @endforeach
    </div>

    <div class="row">
        <div class="col-md-4">
            <div class="earthliving-extension-card">
                <span>Current priority</span>
                <strong>Do not auto-install to production</strong>
                <p>Every panel addon should be tested after a backup and checked against Blueprint/Pterodactyl updates.</p>
            </div>
        </div>
        <div class="col-md-4">
            <div class="earthliving-extension-card">
                <span>Useful next build</span>
                <strong>Velocity Network View</strong>
                <p>Show proxy, Earth Living, Standard Survival and test server health in one owner dashboard.</p>
            </div>
        </div>
        <div class="col-md-4">
            <div class="earthliving-extension-card">
                <span>Research queue</span>
                <strong>Plugin manager + player tools</strong>
                <p>Browse options here, then turn the best ones into Earth Living-safe workflows.</p>
            </div>
        </div>
    </div>
</div>
