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
